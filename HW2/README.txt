Michael Meding
mikeymeding@gmail.com
NLP Fall 2014
10/9/14

This software needs to be used in 3 steps,
First you need to preprocess the incomming raw data.
java -jar PreProcess/dist/PreProcess.jar input.txt > output.txt

Then you need to model that processed data
java -jar CreateModel/dist/CreateModel.jar output.txt [n-gram size] model.out

Finally, you need to run your test data over that model
java -jar TestModel/dist/TestModel.jar test-data.txt model.out [additive-constant]

Samples of each of these files using the sample data given can be found in the /doc directory of each subfolder.
Additionally, you can also find a much smaller bigram model in the /doc directory of the TestModel app.
Both models worked well when tested and returned data that reflected that of manually calculated data.
