# Multidomain Architecture

## Problem Statement / Assumptions

The information to be maintained are Customers, Products and Purchases. A Product can be bought by multiple Customers. 
A Customer can buy multiple Products. We want to be able to manage customer purchases and we want to have a user
interface that allows to search for information about customers (and their products) or products (and the 
customers who have purchased any of them). 

### Solution 

* How many microservices there should be? 

There should **three** different microservices (one for each main business function): 

* A **Customer microservice** which maintains data about the customers in a company. This Microservice offers a REST API
that allows to retrieve customer data. We assume the microservice allows to obtain customers by id and by filtering 
filtering conditions (by name, age, etc.). If a Customer is added or removed an event is emitted to an event bus, so that it can be listened by other microservices. 

The Customer microservice contains a table customer-product that captures whether a customer has bought a product. 

* A **Catalog microservice** which maintains all the data about the products that the company is offering to Customers. We
can assume that this microservice maintains the stock quantities. If a Product is added, removed or modified an event is emitted to an event bus, so that it can be listened by other microservices. 

Catalog microservice contains a table product-customer that captures whether a product has been bought by a customer. 

* A **Purchases microservice** which allows to purchase a Product by a Customer, a certain number of units. This microservice
will expose at least one operation purchase(customer_id, product_id, quantity). It listens to events from the other microservices so that it knows valid product ids, and valid customer ids. 

An event is emitted when a new purchase is done. 

### Scenarios

Search for all customers and find the products they have bought. Interaction with Customer MicroService 

Query to the Customer microservice by 
