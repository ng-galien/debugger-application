# General debugger application

** WORK IN PROGRESS **

## Motivation

The motivation for this project comes after I write a PL/pgSQL debugger for Intellij Ultimate. 
I wanted to share the debugger with a larger community and make it available on others editors and IDEs. 
Behind the scenes, I want experiments DDD way and drill down to the core of the debugger domain.  
Kotlin has been chosen as the language of choice for this project.

## Introduction

The purpose of this application is to provide a simple way to debug for many different languages.  
A agnostic debugger is provided, which can be wired any language with plugins.  

The debugger is exposed with a console and a REST adapter and a Java facade. The execution can be local or dockerized.

The goal is to maintain a isolated domain model which can be used in many IDEs and editors.  

## Features

* Source code management and inspection with ANTLR
* Breakpoint handling
* Variable inspection
* Stack inspection
* Step into, step over, step and continue commands
* Local and remote execution
* Dockerized execution
* Java facade
* REST adapter
* Console adapter
* Language agnostic
* Language plugins
* Persistence of execution state, configuration and breakpoints


