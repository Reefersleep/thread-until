# thread-until

I always wondered why there wasn't a macro like `some->` that short-circuited once an arbitrary predicate function returned a truthy val.

Well, now there is. 

`->until` (pronounced "thread-first-until") does exactly that. 

Observe:
```
(->until {} :error
         (assoc :name "Burt")
         (assoc :error "Wrong guy")
         (assoc :money "One million dollars"))
;=> {:name "Burt", :error "Wrong guy"} ;;Good thing we didn't give the wrong guy a million dollars!
```

Of course, there's a `->>until` ("thread-last-until") as well.

This is mostly just an exercise in macro writing for me, but who knows, it might prove valuable? 

Check the tests to see example usage. 
