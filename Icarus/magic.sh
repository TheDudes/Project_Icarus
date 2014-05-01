#! /bin/bash

./make.sh $1 && cd build/classes && java Icarus.main && cd ../..
