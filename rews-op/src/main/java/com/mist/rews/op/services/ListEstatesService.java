package com.mist.rews.op.services;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mist.rews.RealEstateDatabase;
import com.mist.rews.services.xsd.realestate.ListEstates;
import com.mist.rews.services.xsd.realestate.ListEstatesResponse;
import com.mist.rews.services.xsd.realestate.ObjectFactory;
import com.mist.rews.services.xsd.realestate.RealEstateType;
import com.mist.rews.services.xsd.realestate.RealEstateWithInformation;
import org.apache.camel.Handler;

import java.util.List;

public class ListEstatesService implements RealEstateService {

    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();
    private final Function<RealEstateType, RealEstateWithInformation> TRANSFORM_TO_REAL_ESTATE_WITH_INFORMATION =
        transformToRealEstateWithInformation();

    private final RealEstateDatabase database;

    public ListEstatesService(RealEstateDatabase database) {
        this.database = database;
    }

    @Handler
    public ListEstatesResponse handle(ListEstates listEstates) {
        List<RealEstateWithInformation> estates = Lists.newArrayList(
            Iterables.transform(database.getAllRealEstates(), TRANSFORM_TO_REAL_ESTATE_WITH_INFORMATION));

        return OBJECT_FACTORY.createListEstatesResponse()
            .withRealEstate(estates);
    }

    private Function<RealEstateType, RealEstateWithInformation> transformToRealEstateWithInformation() {
        return input -> OBJECT_FACTORY.createRealEstateWithInformation()
            .withInformation(input.getInformation());
    }

}
