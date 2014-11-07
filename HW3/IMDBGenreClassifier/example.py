#!/usr/bin/env python
"""
This is sample code for Homework Assignment #3 in Fall 2014 91.530/91.460
Natural Language Processing course, @ Computer Science Department @
UMass Lowell.
"""
from __future__ import division
import os
import sys
import nltk
import nltk.metrics.confusionmatrix as cm
from nltk import word_tokenize
from nltk.corpus import brown

new_feature_set = 0

# _DEBUG = 2
# _DEBUG = 1
_DEBUG = 0

CORPUS_DIR = "./imdb_corpus"
# os.chdir(CORPUS_DIR)

class Instance(object):
    """ Stores all information available about a given instance """

    def __init__(self, title, text, label, all_labels):
        self.title = title
        self.text = text
        self.label = label
        self.all_labels = all_labels

    def __repr__(self):
        return "%s | %s | %s" % (self.title, self.label, self.all_labels)


categories = ['Action', 'Adventure', 'Comedy', 'Drama', 'Fantasy', \
              'Mystery', 'Romance', 'Sci-Fi', 'Thriller', 'Western','test-all']


def read_corpus(label, label_dir, text_dir):
    """ Use example:

        read_corpus('Drama', 'labels/all', 'records')
        read_corpus('Drama', 'labels/training', 'records')
        read_corpus('Drama', 'labels/test', 'records')
    """
    if _DEBUG == 2:
        global positive_titles, negative_titles

    if not label in categories:
        raise Exception("read_corpus(): Illegal genre category: %s\n" % label + \
                        "Legal categories are: %s" % categories)

    positive_fname = "positive_%s.txt" % label
    negative_fname = "negative_%s.txt" % label

    try:
        positive_f = open(os.path.join(CORPUS_DIR, label_dir, positive_fname))
        negative_f = open(os.path.join(CORPUS_DIR, label_dir, negative_fname))
    except IOError, e:
        sys.stderr.write("read_corpus(): %s\n" % e)

        # create a dictionary of positive titles, with all labels for each title
    positive_titles = {}
    for line in positive_f:
        segs = [x.strip() for x in line.split('|')]
        title, labels = segs[0], segs[1:]
        positive_titles[title] = labels

    # create a dictionary of negative titles, with all labels for each title
    negative_titles = {}
    for line in negative_f:
        segs = [x.strip() for x in line.split('|')]
        title, labels = segs[0], segs[1:]
        negative_titles[title] = labels

    # create a set of positive and negative instances
    try:
        data = []
        for fname in os.listdir(os.path.join(CORPUS_DIR, text_dir)):
            full_fname = os.path.join(CORPUS_DIR, text_dir, fname)

            title = fname[:-4]  # cut away .txt extension

            all_labels = []
            if title in positive_titles:
                all_labels = positive_titles[title]
                text = open(full_fname).read()
                clabel = label
            elif title in negative_titles:
                all_labels = negative_titles[title]
                text = open(full_fname).read()
                clabel = "Not_%s" % label
            else:
                continue

            # print title, all_labels, clabel
            instance = Instance(title, text, clabel, all_labels)

            data.append(instance)

    except IOError, e:
        sys.stderr.write("read_corpus(): %s\n" % e)

    return data


def get_data_from_dir_list(dir_list):
    language_data = {}
    for dirname in dir_list:
        language_data[dirname] = []
        file_list = os.listdir(dirname)
        for filename in file_list:
            pathname = os.path.join(dirname, filename)
            language_data[dirname].append(open(pathname).read())
    return language_data


def extract_features(instance):
    """
    Extracts features from an instance.
    
    Args:
       instance: an Instance object
    Returns:
       {'feature_name' : feature_value} - feature hashtable
    """
    feature_set = {}
    text = instance.text


    # VERY BAD FEATURE SET
    if new_feature_set:
        feature_set["firstword"] = text.split()[0]
        feature_set["firstletter"] = text[0]
        feature_set["lastword"] = text.split()[-1]
        feature_set["lastletter"] = text[-1]
        return feature_set

    # VERBS
    else:

        tokens = word_tokenize(text)  # tokenize text
        patterns = [
            (r'.*ing$', 'VBG'),  # gerunds
            (r'(The|the|A|a|An|an)$', 'AT'),  # articles
            (r'.*able$', 'JJ'),  # adjectives
            (r'.*ness$', 'NN'),  # nouns formed from adjectives
            (r'.*ly$', 'RB'),  # adverbs
            (r'.*ed$', 'VBD'),  # simple past
            (r'.*es$', 'VBZ'),  # 3rd singular present
            (r'.*ould$', 'MD'),  # modals
            (r'.*\'s$', 'NN$'),  # possessive nouns
            (r'.*s$', 'NNS'),  # plural nouns
            (r'^-?[0-9]+(.[0-9]+)?$', 'CD'),  # cardinal numbers
            (r'.*', 'NN')  # nouns (default)
        ]

        # REALLY BAD POS TAGGER
        tagger = nltk.RegexpTagger(patterns)
        tagged_data = tagger.tag(tokens)

        # get all words tagged as verbs for features
        for word in tagged_data:  # for all words in our tagged data set
            if (word[1] == "VBD" or word[1] == "VBZ"):
                feature_set[word[0]] = word[1]
        return feature_set


def make_training_data(data):
    """
    Args:
       data: list of instances
    Returns:
       [(feature_set, label), ...] 
    """
    training_data = []
    for instance in data:
        feature_set = extract_features(instance)
        label = instance.label
        training_data.append((feature_set, label))
    return training_data


def make_classifier(training_data):
    return nltk.classify.naivebayes.NaiveBayesClassifier.train(training_data)


def split_data(data, category, dev_frac=.3):
    # split the data into training and dev-test portions
    positives = [x for x in data if x.label == category]
    negatives = [x for x in data if x.label == 'Not_%s' % category]

    random.shuffle(positives)
    random.shuffle(negatives)

    dev_size = int(len(positives) * dev_frac)

    dev_set = positives[:dev_size] + negatives[:dev_size]
    training_set = positives[dev_size:] + negatives[dev_size:]

    return training_set, dev_set


def analyze_error(classifier, dev_d, category):
    guess = []
    actual = []
    not_category = "Not_" + category

    for e in dev_d:
        guess.append(classifier.classify(e[0]))
        actual.append(e[1])
        # print "Our Classifier: %s" % guess
        # print "Actual Value: %s" % actual

    # construct confusion matrix with given arrays
    cm = nltk.ConfusionMatrix(actual, guess)
    # print(cm.pp(sort_by_count=True))

    # get counts for calculations
    TP = cm.__getitem__([category, category])  # true positive (our prediction was correct)
    FP = cm.__getitem__([category, not_category])  # false positive (we predict "Not" when the opposite is true)
    FN = cm.__getitem__([not_category, category])  # false negitive (vice versa)

    # get accuracy
    accuracy = nltk.classify.accuracy(classifier, dev_d)

    # check for divide by zeros
    if FN > 0 and FP > 0 and TP > 0:
        precision = TP / (TP + FP)
        recall = TP / (TP + FN)
        f_score = (2 * precision * recall) / (precision + recall)
        return [accuracy, precision, recall, f_score]
    else:
        f_score = 0
        if not TP:
            return [accuracy, 0, 0, f_score]
        if not FP:
            precision = 0
            if not FN:
                recall = 0
            recall = TP / (TP + FN)
            return [accuracy, precision, recall, f_score]
        if not FN:
            recall = 0
            if not FP:
                precision = 0
            precision = TP / (TP + FP)
            return [accuracy, precision, recall, f_score]

def mainloop(category):
    # load up the training and test data (arrays of Instances)
    training_data = read_corpus(category, 'labels/training', 'records')
    #test_data = read_corpus(category, 'labels/test', 'records')

    # split training data into train and dev sets
    training_set, dev_set = split_data(training_data, category, .3)

    # create our classifier using our feature set
    train_d = make_training_data(training_set)
    classifier = make_classifier(train_d)

    # get our dev set to test our classifier on
    dev_d = make_training_data(dev_set)

    # find out how bad it is using confusion matrix
    results = analyze_error(classifier, dev_d, category)  # figure out counts for TP and TN
    return results

def test_set(category):
    # load up the training and test data (arrays of Instances)
    training_data = read_corpus(category, 'labels/training', 'records')
    test_data = read_corpus(category, 'labels/test', 'records')

    # create our classifier using our feature set
    train_d = make_training_data(training_data)
    classifier = make_classifier(train_d)

    # get our dev set to test our classifier on
    dev_d = make_training_data(test_data)

    # find out how bad it is using confusion matrix
    results = analyze_error(classifier, dev_d, category)  # figure out counts for TP and TN
    return results

# ACTUAL MAIN
if __name__ == '__main__':
    import argparse
    import random

    parser = argparse.ArgumentParser(description='Classify movie categories.')
    parser.add_argument('category', action='store', type=str,
                        help="One of 10 Genre Categories: %s" % categories)

    args = parser.parse_args()
    #args = parser.parse_args(['test-all'])
    category = args.category

    if not category in categories:
        sys.stderr.write("Illegal genre category: %s\n" % category)
        parser.print_help()
        sys.exit()

    if category == "test-all":
        categories.remove("test-all") # to avoid errors
        for category in categories: # for all categories
            # get a bunch of results after running on dev data
            result_set = []
            for i in range(20):
                results = mainloop(category)
                # print "trial: ", i, " ", results
                if results.__len__() == 4:  # not exactly sure why this happens
                    result_set.append(results)
                else:
                    continue

            # get the mean of each of the values (acc,prec,recall,f-score)
            sum_accuracy = 0
            sum_precision = 0
            sum_recall = 0
            sum_fscore = 0
            for result in result_set:
                sum_accuracy += result[0]
                sum_precision += result[1]
                sum_recall += result[2]
                sum_fscore += result[3]

            print "averages after running 20 trials using dev set on category :", category
            print "avg accuracy  : ", sum_accuracy / result_set.__len__()
            print "avg precision : ", sum_precision / result_set.__len__()
            print "avg recall    : ", sum_recall / result_set.__len__()
            print "avg f-score   : ", sum_fscore / result_set.__len__()

    else:
        results = test_set(category)
        print "Accuracy : ", results[0]
        print "Precision: ", results[1]
        print "Recall   : ", results[2]
        print "F-Score  : ", results[3]








