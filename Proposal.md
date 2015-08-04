Title:
Detecting sentence boundaries and disfluencies using prosodic and lexical features

Team members:
Anand Madhavan, Tejaswi Tenneti, Elizabeth Lingg

Problem Definition:
Textual output from speech recognition systems typically are a "stream of words". This output usually does not contain punctuation and annotations of disfluencies, which are often identifiable in written text. The partial nature of the output thus adversely affects human readability. In this project, we attempt to automatically identify sentence boundaries and disfluencies, such as fillers, edits and interruption points.

Approach:
We propose to combine prosodic features like duration, fundamental frequency, energy and pause, along with POS tags feature and other features, such as the word, word frequency for the speaker, whether silence follows word etc. We plan to use the Stanford POS tagger to obtain POS tags and use the software Praat to obtain prosodic features. We wish to then use these features to experiment and try out machine learning techniques such as classifiers (SVM, decision trees), HMMs and MEMMs. We intend using reference transcripts for training and evaluation.

Time permitting, we hope to try some of the following:
  * Evaluate our system using speech output transcripts as well.
  * Identify disfluencies that are specific to the speaker.
  * Detect common sources of errors in disfluency detection, such as long edit regions, where the edit itself appears very fluent because it is long (eg: “[whenever they come out with a warning ](and.md) you know they were coming out with a warning about trains ”).

Data Sources:
We will be using the following Conversational Telephone Speech (CTS) and Broadcast News (BN) data for our work:
  * CatalogID: LDC2005T24 (contains annotated disfluencies like sentence units , interruption points and fillers)
  * CatalogID: LDC2005S15 (contains wave files for above)
  * CatalogID: LDC2009T01 (subject to availability)

References:
  * Yang Liu, Elizabeth Shriberg, Andreas Stolcke, Barbara Peskin, Jeremy Ang, Dustin Hillard, Mari Ostendorf, Marcus Tomalin, Phil Woodland, and Mary Harper. 2005. Structural Metatada Research in the EARS Program,. ICASSP 2005.
  * Yang Liu, Elizabeth Shriberg, Andreas Stolcke, Dustin Hillard, Mari Ostendorf, Barbara Peskin, and Mary Harper. 2004. The ICSI-SRI-UW Metadata Extraction System, ICSLP 2004.
  * Snover, Matthew, Bonnie Dorr and Richard Schwartz. 2004. A Lexically-Driven Algorithm for Disfluency Detection. Short Papers Proceedings of HLT-NAACL 2004. Boston: ACL. 157--160.
  * Shriberg, E., et al., “Prosody-based Automatic Segmentation of Speech into Sentences and Topics”, Speech Communication, 32, pp. 127-154, 2000.
  * Rich Transcription Evaluation Project, NIST (rteval: http://www.nist.gov/speech/tests/rt/)