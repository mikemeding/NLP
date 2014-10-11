Michael Meding
NLP Fall 2014
10/9/14

This software needs to be used in 3 steps,
First you need to preprocess the incomming raw data.
java -jar PreProcess/dist/PreProcess.jar input.txt > output.txt

Then you need to model that processed data
java -jar CreateModel/dist/CreateModel.jar output.txt [n-gram size] model.out

Finally, you need to run your test data over that model
java -jar TestModel/dist/TestModel.jar test-data.txt model.out

Samples of each of these files can be found in the /doc directory of each subfolder
