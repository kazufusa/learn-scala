# Learn Caliban-Blog-Series

- https://github.com/ghostdogpr/caliban-blog-series
- [Part 1: How to turn a simple API into GraphQL](https://medium.com/@ghostdogpr/graphql-in-scala-with-caliban-part-1-8ceb6099c3c2)
- [Part 2: Nested effects and query optimization](https://medium.com/@ghostdogpr/graphql-in-scala-with-caliban-part-2-c7762110c0f9)
- [Part 3: Extend your backend capabilities with wrappers](https://medium.com/@ghostdogpr/graphql-in-scala-with-caliban-part-3-8962a02d5d64)
- [Bonus: Caliban Client: a type-safe GraphQL Client for Scala and Scala.js](https://medium.com/@ghostdogpr/caliban-client-a-type-safe-graphql-client-for-scala-and-scala-js-718aa42c5ef7)


## Part1

```sh
$ sbt "runMain pat1.HttpApp"
```

```sh
$ gq http://localhost:8088/api/graphql  -q 'query { findPug(name: "tete") {name pictureUrl }}'
Executing query... done
{
  "data": {
    "findPug": {
      "name": "Patrick",
      "pictureUrl": "https://m.media-amazon.com/images/I/81tRAIFb9OL._SS500_.jpg"
    }
  }
}
```
