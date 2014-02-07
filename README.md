APNS messaging with Netty 5.0
=============================
Introduction
============
This document covers how to use the netty based apns client which gives very high througput.

Features
========
* High Througput
* Select between single and batch transport
* Scalable
* Less memory consumption and added value of Netty's features
* Netty's zero copy used heaving to reduce cpu overhead on network traffic.
* Interfaces for executing Feedback responses in different executor thread
* Configurable properties
* Achieved 100K per m1.large nodes(4 core 8 GB)


Get Started
===========

APNS-Netty is a project which is built on Spring 3 and Netty. The goodness of both frameworks are used but mostly netty weighs up. This uses the latest Netty 5.0 .


To get started you can create a maven project and load the apnsAppContext.xml through the followig code.

AbstractApplicationContext      appContext =  new ClassPathXmlApplicationContext("apnsAppContext.xml");
appContext.registerShutdownHook();

Please do configure CONFIG_BASE environment variable and create an empty property file in CONFIG_BASE/apns/apns.properties.


Model
=====

2 Queues are intialized in code. These queues are used to get batch or single messages as inputs. The processing logic picks them either as a batch or as single and send this to APNS.

How to create a batch and push messages to APNS?
------------------------------------------------

final BatchMessageQueue batchMessageQueue = (BatchMessageQueue) appContext
                        .getBean(
                                ApplicationContextComponents.BATCH_MESSAGE_QUEUE);

                final BatchApnsConnection bConn = (BatchApnsConnection) appContext
                        .getBean(
                                ApplicationContextComponents.BATCH_CONNECTION);

                final BatchIdentifierGenerator batchIdentifierGenerator = (BatchIdentifierGenerator) appContext
                        .getBean(
                                ApplicationContextComponents.BATCH_IDENTIFIER_GENERATOR);


                final BatchFullMessage bfm = new BatchFullMessage();

                for (int i = 0; i < 10; i++) {
                    final ApnsMessage.Builder builder = new ApnsMessage.Builder();
                    
                    builder.deviceToken("xyz");
                    
                    builder.payLoad("{\"aps\" : {\"alert\" : \"Reds trying to hold on to lead."
                                    + i
                                    + "\",\"badge\" : 9,\"sound\" : \"bingbong.aiff\"},\"acme1\" : \"bar\",\"acme2\" : 42}");
                    builder.expTime((int) (Calendar.getInstance()
                            .getTimeInMillis() / 1000 + 86400));
                    builder.id(batchIdentifierGenerator.newIdentifier());
                    builder.prio((byte) 10);

                    bfm.addInvMessages(builder.build());
                }
                batchMessageQueue.pushQueue(bfm);



Configuration
=============

All the configurations are available in default.properties in classpath. 

For dealing with APNS you need a PKCS12 security file. The location of which has to be specified in the external configuration file(apns.properties).

APNSConfig.certificatePath=apns/apnscerts/apn_developer_identity.p12


Additionally you can override all the properties in default.properties in the external configuration file.


## Configuration to connect to APNS
APNSConfig.certificatePath=apns/apnscerts/apn_developer_identity.p12



APNSConfig.production=false
APNSConfig.password=xyzabcd




## Batch related
## Total channels to be created is batchProcssConnCount + batchAcceptConnCount
APNSConfig.batchProcssConnCount=50
APNSConfig.batchAcceptConnCount=25
## Total number of threads in event loop
APNSConfig.batchProcessingCount=20
## Max queue size
APNSConfig.maxBatchQueueSize=10000
## Max queue poller threads
APNSConfig.maxBatchQueuePollerThreads=100



## Single queue
APNSConfig.maxQueueSize=30000


Conclusion
==========
Please send in  your comments. 





