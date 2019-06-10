2018 Accumulo Summit
 Octobter 15, 2018
 
### opening - Donald Hood, ClearEdge

### keynote - Keith Turner, Peterson

#### what's new in Accumulo 2.0
* alpha release 10/14/2018
  * accumulo 1.8.0 - Sep 2016 
  * since 2.0 - 45 contributors, 1,265 commits, 2,911 files changed
  * dependency updated - Java 8 and Hadoop 3
  
* API changes
  * wanted to enable connection configuration with a single config file
  * new entry point
    ```java
    AccumuloClient client = Accumulo.newCient().usingProperties(configFile).build();
    BatchWriter writer = client.createBatchWriter(tableName);
    ```
    * config
      ```java
      instance
      ```
  * key builder changes
    * new
      ```java
      Key k2 = KeyBuilder() ...
      ```
  * fluent mutation
    (mutation is a row with a list of column values)
  * changes to table creation create a table offline
    * provide splits at creation
    * create offline table
    * create iterators before table comes online
  * dropped some deprecated
    * removed mock accumulo
    * removed public API methods using non-API types
    * removed aggregators
    * other deprecated code was removed
    * not all deprecated methods were removed
  * improved scripts
    * performance
  * bulk import changes
    * old
      * client asks master to import files
      * master asks random tablet servers to inspect the files via threadpool making RPC calls
      * tablet server makes RPC call to metadata table
      * RPCs are all synchronous
    * new
      * client does examination of files
      * client can batch calls to name node and metadata table
      * client calls master to load files asynchronously
      * tablet servers make synchronous RPC calls to metadata table
      * bulk import to offline table happens better
      * load plan can provide the file to row range mapping
        * offline table loading with a load plan up to 15x faster than 1.9.2
  * summarizers
    * zero or more per table
    * customer compactors can access summary data
    * new table permission for summary data access
    * two parts
      * collector - sees all data written to rfile
        * accept - sees all k/v entries
        * summarize - write stats
      * combiner - merges map entries
    * rfiles can have multiple summaries each broken down by row ranges
* Apilyzer - API checking plugin
  * verifies that APIs only useAPI types and plugins that are expected
  * also verifies SPIs (service provide interface) access SPI types 
* new SPIs
  * 
  * cache
  * scan executors
    * long running scans can cause problems for short scans
    * in 2.0 multiple scan executors can be created with different prioritizers
    * test
      * 50 threads doing full table scans
      * 5 threads doing small random lookups
      * configured executor with prioritizer that honors priority hints
      * 4 ms avg time with prioritizer
      * 118 ms avg time without prioritizer
* docker support
  * store most config in zookeeper
  * no server config file
  * provide config on command line
  * log to stdout
  * https://github.com/apache/accumulo-docker
    ```bash
    docker run -d --network'+host" accumulo master \
        ...
    ```
* two tier compression
  * allows small file compaction with snappy or zstd
  * allows large files with gzip
* new monitor
* examples moved out to seperate repository
* less static types
* impsort reformats source imports as build time like the IDE would normally do
* tableID type used internally to end confusion with table name versus ID
* deprecated custom class loader and moved class paths to setup scripts
* can now configure memory based using percentages
* moved from jira to github issues
* asynchronous accumulo
  * goal: limit concurrency by memory instad of threads
    * rewrite accumulo as async
    * could wait for Java Project Loom: fibres and continuations
* compaction improvements
  * long running compaction prevent compaction of new files
  * solution
    * multiple compaction executors
    * select executors per compaction
    * configurable prioritizers
* other
  * multiple language accmulo
### An Exploration of 3 Very Different ML Solutions Running on Accumulo
* by Gadalia O'Bryan and Aaron Cordova, presented by Don Miner
* record table: objectives
  * store records under a unique ID
  * optimized for new records in time order
  * bucket id is prepended to distribute new records across tablet serves
  * supports fetching records that match query criteria after consulting index table
  
* indexes and reverse indexes
  * composite index interleaves bytes of multiple keys (z-order curve)
  * TODO z-order
  
* use cases

  * Price Waterhouse Coopers
    * using ML to evaluate risk assoicated with vendors
    * originally manual process could only evaluate each vendor every year or two using excel
    * automated system allowed evaluation on daily basis
    * automated system allowed deeper graph evaluation, i.e. vendors of vendors, etc.
    
  * cyber model dataset
    * replaced postgres solution
    * provided insight not previously available
    * streaming writes during evaluation
    
  * forensic document use case
    * investigation team at a large pharm
    * need OCR over documents to make them searchable
    * needed to be accessible from mobile devices
    * accumulo allowed
      * mixed different types or sources
      * NLP analytics on data to supplement content 
      
### Datawave - Drew Farris and Hannah Pellón

* storage and retrieval engine on top of accumulo
  * ingest workflow & MR
  * API for query and analytics
* essential knowledge
  * hadoop, hdfs, yarn, MR
  * accumulo - iterators, authorizations, shell
  * zookeeper
  * Wildfly 
* expectations - Datawave is not
  * an SQL database
  * a noSQL database
  * a search engine
  
* foundations
  * ingest and query architecture
  * 
  
* data model
  * records 
    * RawRecordContainer, Event, Document
    * fields and content
  * fields
    * can be multi-valued of same type
    * can be indexed for query
    * special fields include index-only, tokenized, and virtual
  * field types and normalizers
    * transform raw field into index entries using a normalizer
      * numbers, ip address, geo, etc.
* overall structure
  * metadata table
    * separate from the accumulo metadata table
    * tracks fields, types, tyeps, normalizers
  * global index table
    * list of all values and which fields they are stored in and what shared contain the value
  * edge table
  * shard table
    * each shard has 
      * field index
        * term field to document pointers
      * record storage
        * shard record/event storage
      * data storage
        * all content for an object in a K/V pair
      * term index
        * tracks term positions within a document
      * edges
        * relationships between field values in a record
       
* query abstractions
  * syntax - how query is described to datawave
  * logic - how query is executed by datawave
    * EventQuery
    * LookupUUID
    * EdgeQuery - records given edge members
    * DiscoveryQuery - records counts by attribute
    * MetricsQuery - find query metrics
    
* analytics
  * implemented as query logics
    * multi-step query
    * iterative query
  * implemented as MapReduce jobs
  
* datawave quickstart
  * shell scripts
  * downloads, install, and configures almost everything for Datawave
    * java, maven, hadooop, zookeeper, accumulo, wildfly
    * datawave ingest and web
  * test framework and sample queries
  * test ssl certs
  * prepares for guided tour
  * troubleshooting guide
  
* ingesting into datawave

* http://code.nsa.gov/datawave
* https://github.com/NationalSecurityAgency/datawave

### fluo bitcoin
* Jim Klucar, Nyla Technologies

* goal: scare you to rewrite your job in fluo
* aka: your distributed analysis engine is giving you wrong answers (probably)
* AccumuloCraft - use Minecraft to control your distributed database

* Fluo is an optimizer for Mapreduce pipelines
  * MR repetitively processes data
  * typical workloads have small incremental changes at the the end
  * Google reduced web index update using Percolator
  * Fluo is Percolator on Accumulo
  
* Bitcoin
  * peer-to-peer gossip network running across the internet
  * ledger of transactions
  * more than one chain can form, longest chain is the definitive chain
  
* Fluo has observers that run on each tablet server
  * workers run observers and handle incremental updates
  
* Fluo best practices
  * be a pessimist, things will fail
  * idempotent actions - you will get to retry
  * avoid transaction collisions - you may continuously retry
  
  
### Ask an Accumulo Expert

* Has anyone been looking at running Accumulo on Java 10/11?
  - Waiting for Java 11, but ony basic testing has been done so far.
   
* Any issues with Accumulo and OpenJDK?
  - So far, not with a 10 node cluster on EC2.

* Does Accumulo run in containers? On Kubernetes?
  - It has been run but not necessarily on production.
  - HDFS is typically running outside the container for persistance. 

* Can you run Accumulo on AWS? Is there anything I need to watch out for?
  - Not if you are going to run use S3 file systems.
  - It can be run if using for HDFS storage.

* What are some barriers to adoption of Accumulo outside of government?
  - robust SQL support.
  - developers preferring JDBC/ODBC connectivity over working with Java API.

  * Does the lack of packaging affect the adoption in commercial spaces?
    - indirectly, yes
      
* How do I contribute to Accumulo? are there any specific areas where you need help?
  - project issue tracker page
  - "wanted" list
  - helping with testing and documentation is always needed by open source projects.
  
* Is it a bad idea to use Summarizers in 2.0 to compute the Cardinality of keys in a table?
  - summarizers output should be very small so that limits usefulness for computing cardinality.
  
* What are the options for doing SQL on Accumulo right now?
  - Presto project (Facebook/Terradata/...) provides extensible SQL query, Accumulo plugin
  exists in Github.

* New features in Accumulo 2.0 are great, but no sense of when it might be production quality. 
When can we look for at least a release roadmap if no release date?
  - as an Apache Project, roll out depends on users interaction with developer community.
  
* How strong is the affinity of the underlying files to the tservers? 
i.e. can I run hdfs table balancer without issues?
  - based on HDFS x3 replication and rebalancing causes blocks to be offline for a period of time.
  - could cause problems for query performance, but hasn't been a big problem in real world so far.
  
* Do ScanExecutors in 2.0 support pre-empting lower priority scans or simply 
take precedence when a thread is allocated?
  - no ScanExecutors do not support pre-emption.

* I'm new to iterators, what are some gotchas I should watch out for?
  - there a lots of gotchas.
  - use the iterator testing framework to find typical gotchas.
    - iterator re-seek
    - iterator state in memory
    - iterator propogating deletes

* Is there a place to find iterator design patterns or see what other people have done?
  - there was a separation of internal system specific iterators out of the public eye.
    - org.apache.accumulo.core package of user iterator examples
    - don't use the system.iterators package as examples.    

Why should I choose Accumulo over HBase? Over MongoDB? Cassandra?
What came first, Datawave or Accumulo?
What’s the most compeling usecase for Accumulo that you’ve seen?
Are there any plans on the Accumulo roadmap to add integrated support for multilevel security beyond the Security API?
