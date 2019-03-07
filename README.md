This repository consists of an implementation of a novel community detection algorithm. Please find the details
of the algorithm in paper.pdf.

It mainly consists of implementations of topic-spcific similarity kernel and Louvain algorithm based community detection.

- Datasets

It includes three datasets, Facebook, G+, and Twitter.

  - Facebook

    The facebook dataset does not consist of topic-specific similarities between nodes.

  - G+

    The topic-specific similarities for G+ dataset can be generated using the program that is included.
    However, we have provided the graph file which includes topic-specific similarities.

  - Twitter

    We could not make the full dataset (with tweets) available because of the data sharing policies.
    The full dataset will be made available upon request. However, we can not include our contact informaiton
    for now due to anonymous submission policy.

- Scripts

  - Community detection accuracy results for three datasets are included in the results_* files.

  - Scripts are also included to generate these results.
