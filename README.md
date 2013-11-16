wiki_indexer
============

Wikipedia Indexer


This is a Wikipedia Indexer which can index Wikipedia articles under

Terms / Authors / Category / Link

Currently the program makes use of a simple XML parser and a Wikipedia parser developed
as per the specifications given in the project specifications (including removal of font styles,
lists, section titles, section text, link parsing, template removal). The Tokenization is done
on Whitespaces, Puntuations, Special characters, Accents, Capitalization, Hyphenation, Stopwords,
Numbers.
The SPIMI algorithm is implemented to perform indexing. This assumes no partitions in the final
index being created.

The retrival is done to obtain top-k articles, value dictionary, term dictionary, boolean retrieval
as well as supports wild card queries.

A utility to generate unique hashes using md5 algorithm is also provided. This is an extension.
