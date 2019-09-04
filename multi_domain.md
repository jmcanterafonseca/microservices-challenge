# Multidomain Architecture

## Problem Statement / Assumptions

The information to be maintained are Customers, Products and Purchases. A Product can be bought by multiple Customers. 
A Customer can buy multiple Products. We want to be able to manage customer purchases and we want to have a user
interface that allows to search for information about customers (and their products) or products (and the 
customers who purchased them). 

### Solution 

* How many microservices there should be? 

There should **three** different microservices (one for each main business function): 

* A **Customer microservice** which maintains data about the customers in a company. This Microservice offers a REST API
that allows to retrieve customer data. We assume the microservice allows to obtain customers by customer id and customers 
that meet certain filtering conditions (by name, age, etc.). If a Customer is added an event is emitted to an event bus. 

* A **Catalog microservice** which maintains all the data about the products that the company is offering to Customers. We
can assume that this microservice maintains the stock quantities. 

* A **Purchases microservice** which allows to purchase a Product by a Customer, a certain number of units. This microservice
will expose at least one operation purchase(customer_id, product_id, quantity). 

### Scenarios

Search for all customers and find the products they have bought. Interaction with Customer MicroService 
