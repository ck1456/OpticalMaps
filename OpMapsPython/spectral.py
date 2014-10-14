# -*- coding: utf-8 -*-
"""
Created on Thu Oct  9 20:25:41 2014

@author: kumar
"""

import numpy as np
import pandas as pd

f = open('./data/test_problem_2_num_0.txt')

#read data in the correct format
count = 0
all_positions = []
max_len = 0
for line in f:
    if count < 2:
        count += 1
        continue
    else:
        #each of these is a molecule
        #print line
        positions = []
        for position in line.split(' '):
            pos = float(position)
            #print pos
            positions.append(pos)
        #print positions
        if len(positions) > max_len:
            max_len = len(positions)
        positions.sort()
        all_positions.append(positions)
        rev_positions = [positions[i] for i in range(len(positions) - 1,0,-1)]
        all_positions.append(rev_positions)
        
n = 0
for positions in all_positions:
    for x in range(max_len - len(positions)):
        positions.append(0)
    all_positions[n] = positions
    n += 1

testcase = pd.DataFrame(all_positions)

#print testcase

for t in testcase:
    print testcase[t].mean()
    testcase[t].replace(0.0,testcase[t].mean(),inplace=True)
#testcase = testcase.replace(0,df[])

#print testcase

#data ready for clustering
from sklearn.cluster import SpectralClustering

model = SpectralClustering(n_clusters=2)
model.fit(testcase)
labels = model.labels_
print np.size(np.where(labels > 0))
print labels