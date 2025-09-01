# AIDA
AIDA is a robot worked on by a group of students in the course TDDD96. The project is requested by IDA. The repo is used to assist in the creation of code.  
In 2025 another group of students continued the development of this project for the course TDP032. Notable additions are a revamped codebase for the app portion of this repo and a web version of the app intended for pupils.

<br/>

## Table of contents
* [Setup](#setup)
* [How to use this repo](#how-to-use-this-repo)
  - [Code development](#code-development)
    - [Steps on your local machine](#steps-on-your-local-machine)
    - [Steps on github.com, aka creating the pull request](#steps-on-githubcom-aka-creating-the-pull-request)
  - [Iterative development](#iterative-development)
  - [Approve a pull request](#approve-a-pull-request)
* [Structure of repo](#structure-of-repo)
* [License](#license)
<br/>

## Setup
You will find guides on how to get the app and the web versions up and running separately:  
1. [App](android/README.md)
2. [Web](web/README.md)

<br/>

## How to use this repo
This is a guide on how to use this git repo, both for code development and for the iterative development, to maintain consistency and to make our work easier. 

### Code development

First and foremost, all development is supposed to happen on different branches, in fact, it is impossible to push to the main branch. Trying to push to main will result in: 

```zsh
remote: error: GH006: Protected branch update failed for refs/heads/main.
remote: error: Changes must be made through a pull request.
To https://github.com/eliasjlara/PUM-04.git
 ! [remote rejected] main -> main (protected branch hook declined)
error: failed to push some refs to 'https://github.com/eliasjlara/PUM-04.git'
```

This is a safety precaution to keep bugs and non-approved code from the most important branch, the main one! To start coding on a ticket, you can follow these steps as a guide: 

#### Steps on your local machine
1. Check that you're on the Development branch using `git branch`, if you're not, do `git checkout Development`.
2. Pull the latest changes from Development using `git pull`.
3. Create a branch with a name relating to the ticket name using `git checkout -b <good-name>`.
4. Implement your changes on this branch.
5. When ready, push your changes following standard procedure: 
   - `git status`
   - `git add <changed files you want to push>`
   - `git commit -m "Make sure to have a good message describing change"`
   - `git push`
6. If this is the first push to the branch you will get something like:
```zsh
remote: 
remote: Create a pull request for 'test-pr-template' on GitHub by visiting:
remote:      https://github.com/eliasjlara/PUM-04/pull/new/test-pr-template
remote: 
To https://github.com/eliasjlara/PUM-04.git
```
Navigate over to the link in the message, or if this isn't the first time you push to the branch, you can find the pull request in the the [Pull requests tab](https://github.com/eliasjlara/PUM-04/pulls) on the repo github page. After this, you are now ready to create a pull request!

<br/>

#### Steps on github.com, aka creating the pull request
1. When clicking the link you should be greeted by a page that looks something like this:
   <img width="1244" alt="Pasted Graphic" src="https://github.com/eliasjlara/PUM-04/assets/94451739/e645c351-6c79-4e77-8cd0-8ff452e8cda3">
   Choose a good title, preferably the name of the ticket and update the description according to the template I have created, you may remove the comments. When ready, press the green `Create pull request` button!
2. You should now be directed to a page that looks like this: <img width="1065" alt="Update README md #10" src="https://github.com/eliasjlara/PUM-04/assets/94451739/169fc8a6-ec10-4fd4-94ba-7da0d680f1ca">
   Congratulations, you have now created a PR! This is where all updates added to the branch will be shown. If you continue  working on the branch, for example if you forgot a bug, each commit will be shown on the timeline, creating a simple way  to keep track of everything that has happened. Also, notice the big red crosses, don't be alarmed, we will take care of this in the next step.
3. Before we can merge this pull request we first need someone else to look at it, this is the safety precaution previously mentioned. To request a review from someone you either click on the cogwheel that belongs to the reviewers tab on the to right of the page, and choose a specific person to review the PR. The easiest way however would probably just be to send the link in discord and ask for a review.
4. Once the PR is reviewed and approved, you can now go ahead and merge the PR into Development!
5. After merging you might be given an option to delete the branch, I would recommend that you do it to keep branches clean but this is up to you. 

Wow, your code feature is now real since it's implemented on Development 🥳🎉. This should be it for someone who is developing, if someone asked you to review a PR, check out the ["Approve a pull request" section](#approve-a-pull-request).

---

### Iterative development
For iterative development kanban boards have been used. The TDDD96 group used a kanban board on GitHub, whilst the TDP032 used a board on Trello. Using a kanban board on GitHub is recommended since that allows for issues to be synced with the cards on the board, and vice versa. This reduces the mess on the board that comes from people forgetting to move their cards.

---

### Approve a pull request
So, someone has asked you to review a pull request... well, don't worry. Here is a guide on how to do it step by step.
1. First off, reviewing a pr isn't anything special, you simply want to check that all the code that the author wants to merge into Development looks good. The simplest way to check is using the `Files changed` tab on the pr page. When actually checking the code, there is no specific way to do this so go ahead and do it in what ever way is best for you, BUT, be thorough, we don't want any errors in Development just because you approved the pull request without looking!
2. If there are any comments you want to add:
   - The simplest way to add a comment is to click the line where you have a comment and then simply write the comment and post it.
   - After the author of the pull request has changed or answered your comments, and it now looks good to you, press the `resolve conversation` button to "remove" the comment.
3. When the pr looks good. Head over to the `Files changed` tab again and press the `Review changes` button, select `Approve` and then submit the review.
4. The pr should now be reviewed and the author can merge it into Development.

<br/>

## Structure of repo
This repo is split in to five parts. Below you will find brief explanations for each one of the parts.
### [AIDA](https://gitlab.liu.se/denab905/AIDA/-/tree/main/AIDA?ref_type=heads)
Code that runs the actual robot

### [Android](https://gitlab.liu.se/denab905/AIDA/-/tree/main/android?ref_type=heads)
The android app written in Kotlin that is used to speak with and send instructions to the robot.

### [Web]()
The web version of the Kotlin app that is used by pupils to create their own programs and then send them to the teacher via a QR-code. The web application can't communicate with the robot directly.

### [Docs]()
Documentation meant for future groups. Read this to get an idea of the system and how all of the different parts work. This folder also contains all the know bugs for both the app and the web application.

### [Third party](https://gitlab.liu.se/denab905/AIDA/-/tree/main/third_party?ref_type=heads)
We dont know what these are used for. We haven't needed to interact with them so proceed with caution.
<br/>

## License
This project is released under the MIT License. For more info, see [MIT License](https://github.com/eliasjlara/PUM-04?tab=MIT-1-ov-file).
