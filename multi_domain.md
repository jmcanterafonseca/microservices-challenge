# Multidomain Architecture

## Problem Statement / Assumptions

The information to be maintained are *Customers*, *Products* and Purchases. A Product can be bought by multiple Customers. 
A Customer can buy multiple Products. We want to be able to manage customer purchases and we want to have a user
interface that allows to search for information about customers (and their products) or products (and the 
customers who have purchased any of them). 

### Solution 

* How many microservices there should be? 

There should **three** different microservices (one for each main business function): 

* A **Customer microservice** which maintains data about the customers in a company. This Microservice offers a REST API
that allows to retrieve customer data. We assume the microservice allows to obtain customers by id and by 
filtering conditions (name, age, etc.). If a Customer is added or removed an event (with the customer id) is emitted to an event bus, so that it can be listened by other microservices. 

The Customer microservice contains a table customer-product that captures whether a customer has bought a product. 

* A **Catalog microservice** which maintains all the data about the products that the company is offering to Customers. 
This Microservice offers a REST API that allows to retrieve customer data. We assume the microservice allows to obtain customers by id and by filtering conditions (product name, etc.).
We can assume as well that this microservice maintains the stock quantities. If a Product is added, removed or modified an event (with the product id) is emitted to an event bus, so that it can be listened by other microservices. 

Catalog microservice contains a table product-customer that captures whether a product has been bought by a customer. 

* A **Purchases microservice** which allows to purchase a Product by a Customer, a certain number of units. This microservice
will expose at least one operation `purchase(customer_id, product_id, quantity)`. It listens to events emitted by the rest of microservices so that it knows valid product ids, and valid customer ids, etc. 

An event is emitted when a new purchase is done, so that the other microservices can keep up to date their information. 

### Scenarios

**Scenario 1**: Search for all customers including information about the products they have bought. The *Controller front-end component* in charge of resolving this request will be in charge of:

1. Interact with the Customer MicroService by querying data through the REST API including an empty filter. The Customer MicroService will execute a couple of queries that will allow to obtain the information of each matching customer as well as the list of product ids associated to each customer. 

2. Interact with the Product MicroService by asking for the information of the products purchased by each customer in the current page (as the response from the Customer microservice only would contain the ids, but not the descriptions or other information). 

** Scenario 2**: Search for all products including information about the customers that have purchased those products. The *Controller front-end component* in charge of resolving this request will be in charge of:

1. Interact with the Product MicroService by querying data through the REST API including an empty filter. The Product MicroService will execute a couple of queries that will allow to obtain the information of each matching product as well as the list of customer ids associated to each product. The latter can be long and it may need pagination as well. 

2. Interact with the Customer MicroService by asking for the information of the customer who purchased each product in the current page (as the response from the Product microservice only would contain ids, but not the names or other information about customers).

### Alternative Architecture

Instead of duplicating the 
