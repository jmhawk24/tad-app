package com.example.touractorappapi;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class TourBucketModelAssembler implements RepresentationModelAssembler<TourBucket, EntityModel<TourBucket>> {

    @Override
    public EntityModel<TourBucket> toModel(TourBucket tourBucket) {
        return EntityModel.of(
                tourBucket,
                linkTo(methodOn(TourBucketController.class).one(tourBucket.getId())).withSelfRel(),
                linkTo(methodOn(TourBucketController.class).all()).withRel("tour-buckets")
        );
    }

}
