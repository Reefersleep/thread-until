# thread-until

### Dependency info

https://clojars.org/com.github.reefersleep/thread-until

### What, why

I always wondered why there wasn't a macro like `some->` that short-circuited once an arbitrary predicate function returned a truthy val.

Well, now there is. 

`until->` (pronounced "thread-first-until") does exactly that. 

Observe:
```
(until-> {} :error
         (assoc :name "Burt")
         (assoc :error "Wrong guy")
         (assoc :money "One million dollars"))
;=> {:name "Burt", :error "Wrong guy"} ;;Good thing we didn't give the wrong guy a million dollars!
```

Of course, there's a `until->>` ("thread-last-until") as well. 

I also added `while->` and `while->>`, for good measure, you can probably guess what they do.
Here's an example, anyway;
```
(let [withdraw! (fn [account amount]
                  (update account :credit - amount))]
  (while-> {:name   "John"
            :credit 600} (comp #(>= % 500) :credit)
           (withdraw! 100)
           (withdraw! 100)
           (withdraw! 400)  ;;Too bad John, gotta save some credit! 200 is all you get.
           (withdraw! 1000))) 
;=> {:name "John", :credit 400}
```


Check the tests to more examples. 

Inspired in part by thoughts such as these: https://medium.com/appsflyerengineering/railway-oriented-programming-clojure-and-exception-handling-why-and-how-89d75cc94c58

To run the tests in Clojure:

```
clj -M:runner
```

And in ClojureScript:
```
clj -M:cljs-runner
```

### Library developer's notes

To build jar:
```
clj -T:build ci
```

To deploy to clojars:
```
env CLOJARS_USERNAME=reefersleep CLOJARS_PASSWORD=clojars_token clj -T:build deploy
```
