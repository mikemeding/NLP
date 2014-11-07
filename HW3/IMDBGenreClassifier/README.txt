Michael Meding
NLP Homework #3 README
11/7/14

If you wish to run this code simply make the example.py executible and run,
	chmod +x example.py
	./example.py [arguments]

I added an extra argument 'test-all' which runs a set of 20 trials over ALL categories and getting the average results from this (check results.txt for output of this)
	./example test-all

WARNING! : running test-all takes quite a bit of time as it is running 20 trials of my binary classifier over ALL genres

Running the code with normal arguments runs it on the test data as expected
	./example Action

Final note: to switch between running the code on my feature set and the original feature set the boolean flag new_feature_set must be adjusted. This flag is set at the very top of the file. (It is set to my feature set by default [value = 0])
