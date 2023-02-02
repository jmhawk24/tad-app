package com.example.touractorappapi;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
public class TourBucketController {

    private final TourBucketRepository tourBucketRepository;

    private final TourBucketModelAssembler assembler;

    TourBucketController(
            TourBucketRepository tourBucketRepository,
            TourBucketModelAssembler assembler
    ) {
        this.tourBucketRepository = tourBucketRepository;
        this.assembler = assembler;
    }

    @GetMapping("/tour-buckets")
    CollectionModel<EntityModel<TourBucket>> all() {
        List<EntityModel<TourBucket>> tourBuckets =
                tourBucketRepository.findAll()
                        .stream()
                        .map(assembler::toModel)
                        .collect(Collectors.toList());

        return CollectionModel.of(tourBuckets, linkTo(methodOn(TourBucketController.class).all()).withSelfRel());
    }

    @GetMapping("/tour-buckets/{id}")
    public EntityModel<TourBucket> one(@PathVariable Long id) {
        TourBucket tourBucket =
                tourBucketRepository.findById(id).orElseThrow(() -> new RuntimeException());

        return EntityModel.of(
                tourBucket,
                linkTo(methodOn(TourBucketController.class).one(id)).withSelfRel(),
                linkTo(methodOn(TourBucketController.class).all()).withRel("tour-buckets")
        );
    }

    @PostMapping("/tour-buckets")
    EntityModel<TourBucket> create(@RequestBody TourBucket newBucket) {
        TourBucket savedBucket = tourBucketRepository.save(newBucket);
        return EntityModel.of(savedBucket,
                linkTo(methodOn(TourBucketController.class).create(newBucket)).withSelfRel());
    }

    @DeleteMapping("/tour-buckets/{id}")
    void delete(@RequestParam Long id) {
        tourBucketRepository.deleteById(id);
    }
}
