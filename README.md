# Cloud Resource Management for Data Processing Pipeline.
This project is developed as a proof-of-concept implementation for my PhD thesis [ref: Samant, Sunil Singh, et al. "Towards End-to-End QoS and Cost-Aware Resource Scaling in Cloud-Based IoT Data Processing Pipelines." 2018 IEEE International Conference on Services Computing (SCC). IEEE, 2018.]
The implementation is based on the following architecture of the resource management framework, and the code includes the implementation for the components directly interacting with the 'Resource Orchestrator' including the resource orchestrator itself:
!![AuraEN](https://user-images.githubusercontent.com/6667076/95416372-48646380-097e-11eb-93e8-0d587be2ac25.png)
## Building the project
The project requires following tools and technologies for building:
- JDK 1.8
- Maven 3.6.3 \
In addition, for testing the resource management on AWS cloud it requires following components:
  - AWS user account, 
  - AWS login credentials for accessing the AWS APIs for launching and managing the ec2 instances
  - MySQL database for locally managing the information about the launched ec2 instances for each service compomnent of the data pipeline[database model supplied].
  - For simplying the service installation and configuration process on newly launched instances for each service component of data pipeline, it is assumed that the AMI with pre-installed services and tools is available for each service type.
  - Please refer to the provided bash file available on each AMI for configuring and re-starting the service components.
  - Finally, the application requires the sustainable QoS profile for each selected ec2 instance types to find out the optimized resource amount for rntire data pipeline services such as ingestion, processing and storage, workload predictor as one-step-ahead data streaming rate provider and required end-to-end data processing delay (as end-to-end latency) as the QoS requirement.
  
Data processing pipeline application:
  - The scripts are provided for running and configuring following service components at each layer of the data processing pipeline: Apache Kafka for data ingestion layer, Apache Spark for stream processing layer and Apache Cassandra for storage layer.\
In addition, it requires an application (available at: https://github.com/samantsunil/data-processor-app ) for submitting the spark streaming jobs in the processing layer which requires the updated connectivity info about the recently updated ingestion layer and storage layer resources. \
Goal:
The goal is to demonstrate how the cloud resources can be requested, configured and managed (scaled) autonomically for running a multi-service, multi-layer data processing pipeline based on the end-to-end QoS requirements at the minimal cost.
Data generator: For testing the DPP deployment and resource scaling on AWS EC2 cloud, the data generator application implemented as Kafka producer application is available at: https://github.com/samantsunil/data-generator. 
