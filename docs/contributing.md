# What this repository is

This repository contains sources of:
1. Reflekt project --- a library for the compile-time reflection in Kotlin
1. Examples of using this project

# Contributing

We love contributions!
We are happy to see an implementation of new features and fixes of the existing bugs.
The current tasks can be found in the [open issues](https://github.com/JetBrains-Research/reflekt/issues) in the project.
If you have some questions or feature requests, please do not hesitate to open new ones.
Also, it is important to discuss new proposed features in more detail before implementing it.

Please, add a comment to the issue, if you're starting work on it.

It is important to add comments to the new functionality as well as detailed descriptions with examples in the DSL.
This will help other developers and users to use them correctly.

## Submitting patches

The best way to submit a patch is to [fork the project on GitHub](https://help.github.com/articles/fork-a-repo/) 
and then send us a [pull request](https://help.github.com/articles/creating-a-pull-request/) 
to the `main` branch via [GitHub](https://github.com).

If you create your own fork, it might help to enable rebase by default
when you pull by executing
``` bash
git config --global pull.rebase true
```
This will avoid your local repo having too many merge commits
which will help keep your pull request simple and easy to apply.

## Checklist

Before submitting the pull request, make sure that you can say "YES" to each point in this short checklist:

- You provided the link to the related issue(s) from the repository;
- You made a reasonable amount of changes related only to the provided issues;
- You can explain changes made in the pull request;
- You ran the build locally and verified new functionality/analyzers;
- You ran related tests locally (or add new ones) and they passed;
- You don't have code-style problems according the [GitHub Actions](https://github.com/JetBrains-Research/reflekt/tree/master/.github/workflows)
- You do not have merge conflicts in the pull request.
