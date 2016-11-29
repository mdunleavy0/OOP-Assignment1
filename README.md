# OOP-Assignment1
Object oriented programming - Assignment 1


This repo is intended to be part of code portfolio, showcasing miscellaneous skills and achievements to potential employers. **This code is not intended for reuse.**

This repo contains code for the first assignment of Object Oriented Programming, a second year module at the Dublin Institute of Technology. The code is written in Scala and uses the [Processing graphics library](https://processing.org/). The assignment is due to be submitted on 29 November 2016.


### Demo Video

[![Video](http://img.youtube.com/vi/IJ4TzwCRns8/0.jpg)](http://www.youtube.com/watch?v=IJ4TzwCRns8)


### Brief
Below is the opening paragraph of the assignment briefing:
> Use Processing to create a UI for a sci-fi movie device like a warp drive, engines, weapons system. You can base it on any movie you like or come up with your own. It should be kinda usable. It should have lots of animation & interactivity and look amazing. It can be as far out as you like. In other words it can be for an alien. You should use all the stuff you are learning on the course.


### Description
My submission is a procedurally generated galactic map, inspired by those found in video games such as Paradox's Stellaris. The map includes multiple levels of depth, with the ability to zoom in and out from a galactic view down to views of individual moons.


### Technical Features
* Functional object oriented programming
* Parallel programming
* Domain specific data collections
* Recursive design
* Visibility culling
* Tessellation
* Two dimensional cameras / projection
* Collision detection
* Vector mathematics
* Non-uniform random distributions
* Procedural generation


### Source Code Structure

* Main: Package containing the executable sketch and classes used to model the galaxy. All modeling classes are stateless 'pure functional' code.
    * Sketch: The executable Processing PApplet. Used to a bridge between Processing the modeling layer and the camera layer.
    * Satellite: Representation of natural satellites such as moons and stars.
    * Orbit: Orbital information such as period and orbit radius.
    * System: Recursive data structure built around a satellite, its orbit and the satellite systems that orbit it.
    * SystemGenerator: A set of procedures for generating randomised systems of various scales.


* Util: Package containing miscellaneous utilities not related to nor dependent upon the modelling layer.
    * Camera: A 2D camera capable of panning, scaling about the centerpoint, and detecting whether shapes are within frame.
    * Rng: An extension of scala.Random with several new distributions for Float generation. Provides seeded, functional random numbers.
    * Vec2: Immutable, 'pure functional' implementation of 2D Float Vectors.


### Controls
* Mouse Left: Pan
* Mouse Right: Lock camera on a satellite
* Mouse Wheel: Zoom
