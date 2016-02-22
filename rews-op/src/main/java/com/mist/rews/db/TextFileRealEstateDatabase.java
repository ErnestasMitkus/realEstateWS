package com.mist.rews.db;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mist.rews.RealEstateDatabase;
import com.mist.rews.StringReader;
import com.mist.rews.op.helpers.RealEstateHelpers;
import com.mist.rews.services.xsd.realestate.RealEstateType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.List;

import static com.mist.rews.op.helpers.Helpers.currentDate;

public class TextFileRealEstateDatabase implements RealEstateDatabase {

    private static final Logger LOGGER = LoggerFactory.getLogger(TextFileRealEstateDatabase.class);

    public static final String DB_FILE_LOCATION = "db.txt";
    public static final String DB_FILE_LOCATION_BACKUP = DB_FILE_LOCATION  + ".bck";
    public static final String DB_FILE_LOCATION_REMOVE = DB_FILE_LOCATION + ".rm";

    private final Object lock = new Object();
    private List<RealEstateType> estates = Lists.newArrayList();

    public TextFileRealEstateDatabase() {
        load();
    }

    @Override
    public List<RealEstateType> getAllRealEstates() {
        synchronized(lock) {
            return estates;
        }
    }

    @Override
    public Optional<RealEstateType> findRealEstate(Predicate<RealEstateType> predicate) {
        synchronized(lock) {
            return Iterables.tryFind(estates, predicate);
        }
    }

    @Override
    public void updateRealEstate(BigInteger id, RealEstateType realEstate) {
        synchronized(lock) {
            for (int i = 0; i < estates.size(); i++) {
                RealEstateType estate = estates.get(i);
                if (estate.getInformation().getId().equals(id)) {
                    if (realEstate != null) {
                        Optional<RealEstateType> same = RealEstateHelpers.findSame(realEstate, estates);
                        if (same.isPresent() && !same.get().getInformation().getId().equals(id)) {
                            RealEstateHelpers.resolveRegisterException(realEstate, same.get());
                        }
                        estates.set(i, realEstate);
                    } else {
                        estates.remove(i);
                    }
                    asyncSave();
                    return;
                }
            }
            throw new RuntimeException("Not found registere real estate with id: " + id.toString());
        }
    }

    public void load() {
        synchronized(lock) {
            File file = new File(DB_FILE_LOCATION);
            if (!file.exists()) {
                LOGGER.warn("Database file(" + file.getAbsolutePath() + ") does not exist. Trying to use backup file.");
                file = new File(DB_FILE_LOCATION_BACKUP);
            }
            if (!file.exists()) {
                LOGGER.warn("Database file(" + file.getAbsolutePath() + ") not found.");
                return;
            }
            List<RealEstateType> realEstateTypes = Lists.newArrayList();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (line.isEmpty()) {
                        continue;
                    }
                    RealEstateType realEstateType = DbHelpers.toRealEstateType(new StringReader(line));
                    if (!RealEstateHelpers.isEmpty(realEstateType)) {
                        realEstateTypes.add(realEstateType);
                    }
                }
                if (!realEstateTypes.isEmpty()) {
                    estates = realEstateTypes;
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public BigInteger registerRealEstate(RealEstateType realEstate) {
        synchronized(lock) {
            BigInteger max = BigInteger.ZERO;
            for (RealEstateType estate : estates) {
                BigInteger id = estate.getInformation().getId();
                max = max.compareTo(id) > 0 ? max : id;
            }

            BigInteger id = max.add(BigInteger.ONE);
            realEstate.getInformation().setId(id);
            realEstate.getInformation().setRegistrationDate(currentDate());
            Optional<RealEstateType> same = RealEstateHelpers.findSame(realEstate, estates);
            if (same.isPresent()) {
                RealEstateHelpers.resolveRegisterException(realEstate, same.get());
            }
            estates.add(realEstate);
            asyncSave();
            return id;
        }
    }

    public void asyncSave() {
        new Thread(this::save, "TextFileRealEstateDatabase - asyncSave").start();
    }

    public void save() {
        File file = new File(DB_FILE_LOCATION);
        File tempFile = new File(DB_FILE_LOCATION_BACKUP);
        File rmFile = new File(DB_FILE_LOCATION_REMOVE);
        synchronized(lock) {

            if (tempFile.exists()) {
                tempFile.delete();
            }
            try(BufferedWriter writer = new BufferedWriter(new PrintWriter(tempFile))) {
                for (RealEstateType estate : estates) {
                    writer.write(DbHelpers.toString(estate) + "\n");
                }
            } catch(IOException e) {
                e.printStackTrace();
            }

            if (!file.exists()) {
                tempFile.renameTo(file);
            } else {
                if (rmFile.exists()) {
                    rmFile.delete();
                }
                file.renameTo(rmFile);
                tempFile.renameTo(file);
                rmFile.delete();
            }
        }
    }
}
