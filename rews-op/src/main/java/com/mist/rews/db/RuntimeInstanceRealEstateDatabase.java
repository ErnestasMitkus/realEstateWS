package com.mist.rews.db;

import com.google.common.collect.Lists;
import com.mist.rews.RealEstateDatabase;
import com.mist.rews.services.xsd.realestate.RealEstateType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.util.List;

import static com.mist.rews.op.helpers.Helpers.currentDate;

public class RuntimeInstanceRealEstateDatabase implements RealEstateDatabase {

    List<RealEstateType> estates = Lists.newArrayList();

    @Override
    public List<RealEstateType> getAllRealEstates() {
        return estates;
    }

    @Override
    public BigInteger registerRealEstate(RealEstateType realEstate) {
        BigInteger max = BigInteger.ZERO;
        for (RealEstateType estate : estates) {
            BigInteger id = estate.getInformation().getId();
            max = max.compareTo(id) > 0 ? max : id;
        }

        BigInteger id = max.add(BigInteger.ONE);
        realEstate.getInformation().setId(id);
        realEstate.getInformation().setConstructionDate(currentDate());
        estates.add(realEstate);
        return id;
    }
}
