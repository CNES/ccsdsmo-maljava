#!/bin/bash

cd malapi ; mvn clean install ; cd -
cd malimpl ; mvn clean install ; cd -
cd malgen ; mvn clean install ; cd -
cd malbinary ; mvn clean install ; cd -
cd maltcp ; mvn clean install ; cd -
cd malspp ; mvn clean install ; cd -
cd maljms ; mvn clean install ; cd -
cd malamqp ; mvn clean install ; cd -
