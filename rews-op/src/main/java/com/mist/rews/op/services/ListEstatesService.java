package com.mist.rews.op.services;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mist.rews.RealEstateDatabase;
import com.mist.rews.services.xsd.realestate.ListEstates;
import com.mist.rews.services.xsd.realestate.ListEstatesFilter;
import com.mist.rews.services.xsd.realestate.ListEstatesResponse;
import com.mist.rews.services.xsd.realestate.ObjectFactory;
import com.mist.rews.services.xsd.realestate.RealEstateType;
import com.mist.rews.services.xsd.realestate.RealEstateWithInformation;
import org.apache.camel.Handler;

import java.util.List;

import static com.mist.rews.op.helpers.EstatesFilterHelpers.predicateFilter;

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
        List<RealEstateType> realEstates = database.getAllRealEstates();

        List<RealEstateWithInformation> estates = Lists.newArrayList(
            Iterables.transform(
                filterRealEstates(realEstates, listEstates.getFilter()), TRANSFORM_TO_REAL_ESTATE_WITH_INFORMATION));

        return OBJECT_FACTORY.createListEstatesResponse()
            .withRealEstate(estates);
    }

    private Function<RealEstateType, RealEstateWithInformation> transformToRealEstateWithInformation() {
        return input -> OBJECT_FACTORY.createRealEstateWithInformation()
            .withInformation(input.getInformation());
    }

    private List<RealEstateType> filterRealEstates(List<RealEstateType> estates, final ListEstatesFilter filter) {
        if (filter == null) {
            return estates;
        }

        Iterable<RealEstateType> result = estates;

        result = Iterables.filter(result, predicateFilter(filter.getOwner()));
        result = Iterables.filter(result, predicateFilter(filter.getLocation()));
        result = Iterables.filter(result, predicateFilter(filter.getEstateType()));
        result = Iterables.filter(result, predicateFilter(filter.getPrice()));
        result = Iterables.filter(result, predicateFilter(filter.getRegistrationDate()));
        result = Iterables.filter(result, predicateFilter(filter.getConstructionDate()));

        return Lists.newArrayList(result);
    }

}
