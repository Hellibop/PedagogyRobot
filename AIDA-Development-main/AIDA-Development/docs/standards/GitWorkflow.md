### **From an issue**

When viewing an issue, you can create an associated branch directly from that page. Branches created this way use the [default pattern for branch names from issues](https://docs.gitlab.com/ee/user/project/repository/branches/#configure-default-pattern-for-branch-names-from-issues), including variables.

Prerequisites:

* You must have at least the Developer role for the project.

To create a branch from an issue:

1. On the left sidebar, select **Search or go to** and find your project.  
2. Select **Plan \> Issues** and find your issue.  
3. Below the issue description, find the **Create merge request** dropdown list, and select to display the dropdown list.  
4. Select **Create branch**. A default **Branch name** is provided, based on the [default pattern](https://docs.gitlab.com/ee/user/project/repository/branches/#configure-default-pattern-for-branch-names-from-issues) for this project. If desired, enter a different **Branch name**.  
5. Select **Create branch** to create the branch based on your project’s [default branch](https://docs.gitlab.com/ee/user/project/repository/branches/default.html).

####  Steps on your local machine

1\. Check that you're on the Development branch using `git branch`, if you're not, do `git checkout Development`.  
2\. Pull the latest changes from Development using `git pull`.  
3\. Create a branch with a name relating to the ticket name using `git checkout -b <good-name>`. // Edit this step?  
4\. Implement your changes on this branch.  
5\. When ready, push your changes following standard procedure

### 

### **Create merge request**

[https://docs.gitlab.com/ee/user/project/merge\_requests/creating\_merge\_requests.html](https://docs.gitlab.com/ee/user/project/merge_requests/creating_merge_requests.html)

## **View merge requests**

To view all merge requests on the homepage, use the Shift \+ m [keyboard shortcut](https://docs.gitlab.com/ee/user/shortcuts.html), or:

1. On the left sidebar, select the **Merge requests** icon.

or:

1. On the left sidebar, select **Search or go to**.  
2. From the dropdown list, select **Merge requests**.

