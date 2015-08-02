# -*- coding: utf-8 -*-
"""Module for finding anagrams"""

import itertools
import sys


class AnagramFinder(object):
    def __init__(self, filename="words2.txt"):
        self.read_dict(filename)

    def get_anagrams(self, word):
        '''Main method.'''
        two_word_anagram = self.get_two_word_anagram(word)
        most_word_anagram = self.get_most_word_anagram(word)
        print two_word_anagram
        print most_word_anagram

    def get_two_word_anagram(self, word):
        '''Gets the first key from the generator and looks up a corresponding
        word.
        '''
        try:
            keys = self.gen_two_word_anagram_keys(word).next()
        except StopIteration:
            return ''
        return (self.valid_words[len(keys[0])][keys[0]][0],
                self.valid_words[len(keys[1])][keys[1]][0])

    def gen_two_word_anagram_keys(self, word):
        '''Build a generator of tuples of sorted words that represent
        valid anagrams.
        '''
        sorted_word = sort_chars(word)
        for p in bifurcate_int(len(word)):
            small_words = self.gen_subwords(sorted_word, p[0])
            big_words = self.gen_subwords(sorted_word, p[1])
            if p[0] == p[1]:
                # small_words and big_words are the same, so we can use
                # combinations instead of a direct product for a smaller
                # search space. This way we can avoid searching both
                # ('ab', 'ad') and ('ad', 'ab').
                possible_pairs = itertools.combinations_with_replacement(
                        small_words, 2)
            else:
                possible_pairs = itertools.product(small_words, big_words)
            for pair in possible_pairs:
                if merge_words(pair) == sorted_word:
                    yield pair

    def get_most_word_anagram(self, word):
        '''Gets the first key from the generator and looks up a corresponding
        word.
        '''
        try:
            keys = self.gen_longest_anagram_keys(word).next()
        except StopIteration:
            return ''
        return tuple([self.valid_words[len(keys[i])][keys[i]][0]
                     for i in range(len(keys))])

    def gen_longest_anagram_keys(self, word):
        '''Build a generator of tuples of sorted words that represent
        valid anagrams.
        '''
        max_words = len(word)/2
        sorted_word = sort_chars(word)
        for i in xrange(max_words, 1, -1):
            for partition in partition_int(len(word), i):
                words = []
                for length in partition:
                    words.append(self.gen_subwords(sorted_word, length))
                possible_pairs = itertools.product(*words)
                for pair in possible_pairs:
                    if merge_words(pair) == sorted_word:
                        yield pair

    def gen_subwords(self, sorted_word, length):
        for subword in self.valid_words[length].keys():
            if is_subword(subword, sorted_word):
                yield subword


    def read_dict(self, filename):
        '''Reads a text file of words into memory.

        Splits each length of word into its own dict.
        Each dict has sorted strings as keys,
        and sets of unsorted strings as values.

        Returns valid_words, a list of dicts.
        valid_words[n] is the dict of all words of length n.
        valid_words[0] and valid_words[1] are empty dicts.
        '''
        with open(filename) as wordfile:
            max_length = 10
            # add 1 so that valid_words[i] is words of length i
            valid_words = [{} for _ in range(max_length + 1)]
            old_word = ''
            for word in wordfile:
                # remove trailing newline and fix case
                word = word.strip().lower()
                # remove some duplicates that appear next to each
                # other in the alphabetical word list.
                if old_word == word:
                    continue
                old_word = word
                l = len(word)
                if l < 2:
                    continue
                if l > max_length:
                    for _ in range(max_length, l):
                        valid_words.append({})
                    max_length = l
                key = sort_chars(word)
                valid_words[l].setdefault(key, []).append(word)

        self.valid_words = valid_words


def sort_chars(word):
    word_array = [c for c in word]
    word_array.sort()
    return ''.join(word_array)

def merge_two_words(word1, word2):
    '''Merge two sorted strings into one sorted string.

    This is just the "merge" part of mergesort,
    converting to a string at the end.
    '''
    i, j = 0, 0
    out = []
    l1, l2 = len(word1), len(word2)
    while i < l1 and j < l2:
        if word1[i] < word2[j]:
            out.append(word1[i])
            i = i + 1
        else:
            out.append(word2[j])
            j = j + 1
    while i < l1:
            out.append(word1[i])
            i = i + 1
    while j < l2:
            out.append(word2[j])
            j = j + 1
    return ''.join(out)

def merge_words(words):
    '''Split an iterable into singletons and merge them.
    This could be used in place of sort_chars.
    '''
    if len(words) == 1:
        return words[0]
    if len(words) == 2:
        return merge_two_words(words[0], words[1])
    words1 = words[:len(words)/2]
    words2 = words[len(words)/2:]
    return merge_two_words(merge_words(words1), merge_words(words2))

def is_subword(subword, word):
    '''Checks if a sorted subword is a subset of a sorted word.'''
    i, j = 0, 0
    while j < len(word) and i < len(subword):
        if subword[i] == word[j]:
            i = i + 1
            j = j + 1
        else:
            j = j + 1
    if i == len(subword):
        return True
    else:
        return False

def bifurcate_int(i):
    '''Returns a generator of all partitions of i into 2 smaller integers
    that sum to i. Returns evenly divided partitions first, lopsided ones last
    '''
    for j in xrange(i/2, 1, -1):
        yield (j, i - j)

def partition_int(i, num_parts=3):
    '''Returns a generator of all partitions of i into several smaller
    integers that sum to i.
    '''
    partition = [2] * num_parts
    partition[0] = i - 2 * (num_parts - 1)
    if partition[0] < 2:
        return
    yield partition[:]
    while next_partition(partition):
        yield partition[:]

def next_partition(partition):
    '''Modifies partition to the next valid sorted partition.
    If successful, return True, otherwise False.
    '''
    for i in range(len(partition) - 1, 0, -1):
            if partition[i-1] - partition[i] > 1:
                partition[i] = partition[i] + 1
                partition[i-1] = partition[i-1] - 1
                return True
    return False



if __name__ == '__main__':
    if len(sys.argv) < 2:
        print "Please specify a word to anagram"
    else:
        af = AnagramFinder()
        word = ''.join(sys.argv[1:])
        af.get_anagrams(word)
