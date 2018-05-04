# maljava
CCSDS MO MAL JAVA API AND IMPLEMENTATION

This project is an implementation of the [CCSDS MO Message Abstraction Layer (MAL) Standard](https://en.wikipedia.org/wiki/CCSDS_Mission_Operations) in Java language. The MAL Java API is standardized by the CCSDS and its final specification is available:

- [CCSDS 521.0-B-2, Mission Operations Message Abstraction Layer -- Java API](https://public.ccsds.org/Pubs/523x1m1.pdf)

CCSDS Mission Operation implementations for other languages (e.g. C, Go, etc.) can be found on the [CCSDS MO WebSite](http://ccsdsmo.github.io/)

Complete MAL specification can be found on the [CCSDS website](http://public.ccsds.org/publications/BlueBooks.aspx) in the *published documents* section.

In particular:

- [CCSDS 521.0-B-2, Mission Operations Message Abstraction Layer](https://public.ccsds.org/Pubs/521x0b2e1.pdf)
- [CCSDS 524.1-B-1, Mission Operations -- MAL Space Packet Transport Binding and Binary Encoding](https://public.ccsds.org/Pubs/524x1b1.pdf)
- [CCSDS 524.2-B-1, Mission Operations -- MAL Binding to TCP/IP Transport and Split Binary Encoding](https://public.ccsds.org/Pubs/524x2b1.pdf)

## ABOUT

This CCSDS MO MAL Java API was originally developed for the [CNES](http://cnes.fr), the French Space Agency, by [ScalAgent](http://www.scalagent.com/en/spatial-41/products-42/overview-47), a french company specialized in distributed technologies.
All contributions are welcome.

## PROJECT DOCUMENTATION

A MAL/Java description and user's guide is available in the doc directory.

### MAL/Java Description

This Java API implementation mainly includes 5 packages:

  - **malapi** package defines all generic MAL Concepts described by the API: message, data types, etc.
  - **malimpl** package contains the MAL Java implementation, it defines the high level broker, consumer and provider.
  - **malgen** package defines the generic part of transport technology.
  - **malbinary** package includes encoding technologies, in particular implementations for binary (fixed and varint) and split-binary encoding.
  - **maltcp** package includes the implementation of TCP/IP transport binding.
  
Implementations of other transports are provided in 3 additional packages:

  - **malspp** package includes a Space Packet transport implementation conform to the [specification](https://public.ccsds.org/Pubs/524x1b1.pdf).
  - **maljms** package includes a MAL transport implementation over the JMS 1.1 API.
  - **malamqp** package includes a MAL transport implementation over the AMQP v0.9.1 API.

### Practical Tips for Using the MAL Java API

This [tutorial](https://github.com/esa/CCSDS_MO/wiki/Practical-Tips-for-Using-the-MAL-Java-API) contains an introduction to the use of MAL Java API.
