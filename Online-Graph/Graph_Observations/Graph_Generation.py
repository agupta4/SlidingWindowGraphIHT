"""
coding: utf-8

@author: Abhishek Gupta
"""
import pandas as pd
import numpy as np
from matplotlib import pyplot as plt

#EMS Stat function with singleNodeInitial = False
df = pd.read_csv("Results-false.txt", sep = " ", header = None);
df.columns = ["Precision", "Recall", "F-measure", "FuncValue"]
plt.plot(df.index, df["Precision"], 'r-')
plt.plot(df.index, df['Recall'], 'b-')
plt.plot(df.index, df['F-measure'], 'g-')
plt.yticks(np.arange(0.9, 1.05, 0.01))
plt.legend(['precision', 'recall', 'f-measure'], loc = 'lower left')
plt.ylabel('Accuracy Metric')
plt.xlabel('Snapshots')
plt.show()
plt.plot(df.index, df["FuncValue"], 'ro')
#plt.yticks(np.arange(0.0, 1.5, 0.2))
plt.xlabel('Snapshots')
plt.ylabel('Function Value')
plt.show()

#EMS Stat function with singleNodeInitial = True
df = pd.read_csv("Result-true.txt", sep = " ", header = None);
df.columns = ["Precision", "Recall", "F-measure", "FuncValue"]
plt.plot(df.index, df["Precision"], 'r-')
plt.plot(df.index, df['Recall'], 'b-')
plt.plot(df.index, df['F-measure'], 'g-')
#plt.yticks(np.arange(0.9, 1.1, 0.05))
plt.legend(['precision', 'recall', 'f-measure'], loc = 'lower left')
plt.ylabel('Accuracy Metric')
plt.xlabel('Snapshots')
plt.show()
plt.plot(df.index, df["FuncValue"], 'ro')
#plt.yticks(np.arange(0.0, 1.5, 0.2))
plt.xlabel('Snapshots')
plt.ylabel('Function Value')
plt.show()


# In[46]:

#LR Reg function with singleNodeInitial = False
df = pd.read_csv("LRFailed-false.txt", sep = " ", header = None);
df.columns = ["FuncValue", "Precision", "Recall", "F-measure"]
plt.plot(df.index, df["Precision"], 'r-')
plt.plot(df.index, df['Recall'], 'b-')
plt.plot(df.index, df['F-measure'], 'g-')
plt.yticks(np.arange(0.0, 1.5, 0.2))
plt.legend(['precision', 'recall', 'f-measure'], loc = 'upper center')
plt.ylabel('Accuracy Metric')
plt.xlabel('Snapshots')
plt.show()
plt.plot(df.index, df["FuncValue"], 'ro')
plt.yticks(np.arange(0.0, 1.5, 0.2))
plt.xlabel('Snapshots')
plt.ylabel('Function Value')
plt.show()

#LR Reg with singleNodeInitial = true
df = pd.read_csv("LRFailed-true.txt", sep = " ", header = None);
df.columns = ["FuncValue", "Precision", "Recall", "F-measure"]
plt.plot(df.index, df["Precision"], 'r-')
plt.plot(df.index, df['Recall'], 'b-')
plt.plot(df.index, df['F-measure'], 'g-')
plt.yticks(np.arange(0.0, 1.5, 0.2))
plt.legend(['precision', 'recall', 'f-measure'], loc = 'upper center')
plt.ylabel('Accuracy Metric')
plt.xlabel('Snapshots')
plt.show()
plt.plot(df.index, df["FuncValue"], 'ro')
plt.yticks(np.arange(0.0, 1.5, 0.2))
plt.xlabel('Snapshots')
plt.ylabel('Function Value')
plt.show()


df1 = pd.read_csv('GraphMP-LR_PerformanceMetric', sep = " ", header = None)
df1.columns = ["Precision", "Recall", "F-measure", "Time"]
df1['Sparsity'] = [0.05, 0.1, 0.15, 0.2, 0.25, 0.3, 0.35, 0.4, 0.45, 0.5]


df2 = pd.read_csv("GraphIHT_PhaseIII_PreRec.txt", sep = ' ', header = None)
df2.columns = ["Precision", "Recall", "F-measure", "Time"]
df2['Sparsity'] = [0.05, 0.1, 0.15, 0.2, 0.25, 0.3, 0.35, 0.4, 0.45, 0.5]
plt.plot(df1['Sparsity'], df1["Precision"], 'r-')
plt.plot(df2['Sparsity'], df2['Precision'], 'b-')
plt.yticks(np.arange(0.0, 1.2, 0.1))
#plt.xticks(np.arange(0.05, 0.5, 0.05))
plt.legend(['Graph-MP', 'Graph-IHT'], loc = 'center')
plt.xlabel('Sparsity')
plt.ylabel('Precision')
plt.show()

df1 #<--GraphMP
df2#<--GraphIHT


plt.plot(df1['Sparsity'], df1["Recall"], 'r-')
plt.plot(df2['Sparsity'], df2['Recall'], 'b-')
plt.yticks(np.arange(0.0, 1.2, 0.1))
plt.legend(['Graph-MP', 'Graph-IHT'], loc = 'lower left')
plt.xlabel('Sparsity')
plt.ylabel('Recall')
plt.show()


plt.plot(df1['Sparsity'], df1["F-measure"], 'r-')
plt.plot(df2['Sparsity'], df2['F-measure'], 'b-')
plt.yticks(np.arange(0.0, 1.2, 0.1))
plt.legend(['Graph-MP', 'Graph-IHT'], loc = 'lower left')
plt.xlabel('Sparsity')
plt.ylabel('F-measure')
plt.show()

plt.plot(df1['Sparsity'], df1["Time"], 'r-')
plt.plot(df2['Sparsity'], df2['Time'], 'b-')
#plt.yticks(np.arange(0.0, 1.2, 0.1))
plt.legend(['Graph-MP', 'Graph-IHT'], loc = 'upper right')
plt.xlabel('Sparsity')
plt.ylabel('Running Time')
plt.show()



