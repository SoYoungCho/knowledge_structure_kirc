# Knowledge Structure Ver 1.0														
### Copyright(c) 2020.10.07 All rights reserved by Knowledge Innovation Research Center
	Last modified in 2020.11

### DESCRIPTION
This program is composed of 8 packages. 
	- com.kirc.core.sample
	- f1.com.kirc.core.config
	- f2.com.kirc.core.nlp
	- f3.com.kirc.core.coTable
	- f4.com.kirc.core.model
	- f5.com.kirc.core.output
	- option.com.kirc.core.output.localgraph
	- option.com.kirc.core.output.webgraph

and each packages contains source codes of conceptual seperation. 

	1) [com.kirc.core.sample] contains the very main method to run this program.
	2) [f1.com.kirc.core.config] contains a configuration loader, while config.properties contains all the local settings to run the program.
	3) [f2.com.kirc.core.nlp] analyses the input text and seperate each terms considering Korean and English.
	4) [f3.com.kirc.core.coTable] calculates cotable of cooccurence and cosign-similarity, sentence-wise and paragraph-wise.
	5) [f4.com.kirc.core.model] contains source codes which generates Proximity Model with Pathfinder algorithm.
	6) [f5.com.kirc.core.output] contains textual output generator. The texts are represented by json format 1, json format 2, list of edges, and list of vertexes.
	7) [option.com.kirc.core.output.localgraph] contains jar-based local graph generator exploiting the result.
	8) [option.com.kirc.core.output.webgraph] contains html-based web graph generator exploiting the result.


### INSTALLATION
	no installation is required to run this software since it will never be released as it is. 

### DEPENDENCIES
##### Graph Library
	Web) vis.zip, http://visjs.org/index.html#download_install
	Local) JPathfinder.jar, http://interlinkinc.net/

##### NLP Library
	Korean) KOMORAN, http://www.shineware.co.kr
	English) stanford-postagger-3.6.0.jar, http://nlp.stanford.edu/software/tagger.shtml
	

### Knowledge Graph Generator
[Readme : Knowledge Graph Generator](https://github.com/cheonsol-lee/knowledge_structure_kirc/blob/master/Readme(jupyter).md)

### LICENSE
	Copyright(c)2020 All rights reserved by Knowledge Innovation Research Center

### CREDIT
	Cheonsol Lee
	Moojin Kim
	JAYONG KIM
 	Jun Woo Kim
	WON EUI HONG
 	JUN YOUNG PARK
 	SANSUNG KIM
 	Sung Chan Kim
 	JUNG HUN KIM
 	KYUNG JIN KIM
 	DONG HWAN BAE
 	HYUNG WOO KIM
