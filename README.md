# thread-until

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
