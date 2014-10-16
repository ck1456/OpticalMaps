#!/bin/bash

datadir="data/"
solndir="soln/"
for p in 0 1 2 3
do
  for n in 0 1 2
  do
    infile="${datadir}test_problem_${p}_num_${n}.txt"
    outfile="${solndir}test_problem_${p}_num_${n}_soln.txt"
    goldfile="${datadir}test_problem_${p}_num_${n}_gold.txt"
    paramfile="${datadir}test_problem_${p}_num_${n}_parameters.txt"

    java -jar MapResolver.jar ${infile} ${outfile}
    
    echo "${infile}"    
    cat ${outfile} ${goldfile} ${paramfile} | java -jar spec/optical-map-spec.jar
  done
done
