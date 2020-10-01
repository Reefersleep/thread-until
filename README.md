# thread-until

I always wondered why there wasn't a macro like `some->`, but instead of short-circuiting on nil, it'd short-circuit once an arbitrary predicate function returned truthy.

Well, now there is.  `->until` (pronounced "thread-first-until") does exactly that. Of course, there's a `->>until` ("thread-last-until") as well.

This is mostly just an exercise in macro writing for me, but who knows, it might prove valuable? 

Check the tests to see example usage. 
