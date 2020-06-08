# Cloud Resource Management for Data Processing Pipeline.
This project is developed as a proof-of-concept implementation for my PhD thesis [ref: Samant, Sunil Singh, et al. "Towards End-to-End QoS and Cost-Aware Resource Scaling in Cloud-Based IoT Data Processing Pipelines." 2018 IEEE International Conference on Services Computing (SCC). IEEE, 2018.]
The implementation is based on the following architecture of the resource management framework, and the code includes the implementation for the components directly interacting with the 'Resource Orchestrator' including the resource orchestrator itself:
![Resource Managemement Framework Architecture](https://user-images.githubusercontent.com/6667076/83988787-add02580-a987-11ea-8e03-7e9a0abef702.png)
## Building the project
The project depends on following tools and technologies for building:
- JDK 1.8
- Maven 3.6.3 \
In addition, for testing the resource management on AWS cloud it requires following components:
  - AWS user account, 
  - AWS login credentials for accessing the AWS APIs for launching and managing the ec2 instances
  - MySQL database for locally managing the information about the launched ec2 instances for each service compomnent of the data pipeline.
  - For simplying the service installation and configuration process on newly launched instances for each service component of data pipeline, it is assumed that the AMI with pre-installed services and tools is available for each service type.
  - Please refer the provided bash file available on each AMI for configuring the starting the service components.
  - Finally, the application requires the sustainable QoS profile for each selected ec2 instance types for running each of the data pipeline components such as ingestion, processing and storage, workload predictor as one-step-ahead data streaming rate prediction and end-to-end data processing delay (as end-to-end latency) requirement.
  
Data processing pipeline application:
  - The scripts are provided for running and configuring following service components at each layer of the data processing pipeline: Apache Kafka for data ingestion layer, Apache Spark for stream processing layer and Apache Cassandra for storage layer.
Goal:
The goal is to demonstrate, how the cloud resources can be managed (scaled) autonomically on the selected cloud provider for the multi-service multi-layer data processing pipeline considering the end-to-end QoS requirements at the minimal cost.
