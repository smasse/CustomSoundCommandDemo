# SoundCommandApps #

GitLab project SoundCommandApps contains applications to execute sound commands from dolphins 
or other participants via DC Dolphin Communicator app.

# Application SC Sound Command Demo #

Application *SC Sound Command Demo* is from Android project CustomSoundCommandDemo; 
it includes sm.app.sc.customsoundcommanddemo.MainActivity

SC Sound Command Demo is designed to demonstrate the execution of custom sound commands 
emitted by dolphins (or others) and captured and recognized in real-time 
by the DC Dolphin Communicator app, also for Android. 
The human user can define the frequencies of a sound command in DC, 
a dolphin or other participant emits the sound command, DC recognizes it in real-time, 
sends it to this app, which executes the programmed logic for it, and sends the results back to DC.

Using this app as an example, Android programmers can write their own app 
to process customized sound command in DC.

This app is also designed to demonstrate the effectiveness of DC for implementing sound command 
and develop automated responses to dolphin signals.

For example, you can have dolphins learn a specific command to call for medical assistance,
with DC running on a tablet with a hydrophone and with no staff present,
your sound command execution app could send an alert email to staff
or an http URL to a server which would execute some function.
This may be useful in an authentic sanctuary.


#### How to quickly test the custom sound command feature with DC as-is, without emission: ####

1. This text assumes that you are using DC already.
2. Install the *SC Sound Command Demo* app (free from Google Play, no ads).
3. Run *SC Sound Command Demo* app to ensure all is fine.
4. In DC, select the menu option, 3 dots, and *Sound Command Results*
   this will open CustomSoundCommandResultsReceiver
5. Select button *SEND TEST COMMAND SC-TEST*
   this will send sound command 'sc-test' to the app identified in the setting
   naming the third party app; if the setting has not been edited, then this
   will send 'sc-test' to app CustomSoundCommandDemo.
6. App CustomSoundCommandDemo will open
7. Select button EXECUTE
8. Select button SEND SUCCESS TO DC
   This will send a success message to DC, DC message receiver screen will be shown
9. In DC CustomSoundCommandResultsReceiver,
   inspect the text to ensure that all is well
10. The test is complete and you can use the CLOSE button,
    which will show app CustomSoundCommandDemo, because it is next in the stack;
    this will show the previous screen for DC CustomSoundCommandResultsReceiver,
    because it is next in the stack (the data there is not recent);
    then you can use the CLOSE button or the back key to show DC main screen
    which is the spectrogram and the console,
    which will contain main events and sound command execution results text.
11. You can use buttons SAVE TEXT or SAVE ALL or EXPORT DB
    to save this text to the database, etc.
    You can also do a screenshot for future reference.


#### How to test the custom sound command feature using an actual sound command: ####

1. This text assumes that you are using DC already.
2. Install the *SC Sound Command Demo* app (free from Google Play, no ads).
3. Run *SC Sound Command Demo* app to ensure all is fine.
4. In DC, select the SELF-TEST button
   This will make DC process the whistles that it emits as if they are from a participating
   entity and not from itself, because normally DC does not listen to its own whistles.
5. In the edit text field to the left of the EMIT button, type sc-test  
   ('sc-test' is the name of the hard-coded sound command for testing this new feature,
   all sound command names in DC start with 'sc-')
6. Select the EMIT button;  
   this will play the sc-test whistle which is hard-coded and designed to trigger  
   the transmission of the command (sc-test) to CustomSoundCommandDemo;  
   The name of CustomSoundCommandDemo is defined in a setting that can be edited to use your own  
   app name later.  
   The app CustomSoundCommandDemo is run and is shown by Android.
7. Select the EXECUTE button which will generate a test text in the results field.
8. Select the SEND SUCCESS TO DC button, and this will send the results back to DC.
9. DC's special activity CustomSoundCommandResultsReceiver will show up.  
   The results will also be shown in the main screen text (the chat text, aka. console)
10. Select the CLOSE button.  
    This will close the screen and show the CustomSoundCommandDemo which is behind it in the stack.
11. Use the back key to show DC main screen.  
    The results are in the text in the console (yellow text over the spectrogram).



### Release 1 - 2022-2-12 ###

<!--
The DC project includes a demo app that you can modify or use as an example to implement your own command execution.
See detailed how-to in User Guide (in the app)
-->

<!--
## Getting started

To make it easy for you to get started with GitLab, here's a list of recommended next steps.

Already a pro? Just edit this README.md and make it your own. Want to make it easy? [Use the template at the bottom](#editing-this-readme)!

## Add your files

- [ ] [Create](https://gitlab.com/-/experiment/new_project_readme_content:35aa787b0e03b0e42725936ae8605de8?https://docs.gitlab.com/ee/user/project/repository/web_editor.html#create-a-file) or [upload](https://gitlab.com/-/experiment/new_project_readme_content:35aa787b0e03b0e42725936ae8605de8?https://docs.gitlab.com/ee/user/project/repository/web_editor.html#upload-a-file) files
- [ ] [Add files using the command line](https://gitlab.com/-/experiment/new_project_readme_content:35aa787b0e03b0e42725936ae8605de8?https://docs.gitlab.com/ee/gitlab-basics/add-file.html#add-a-file-using-the-command-line) or push an existing Git repository with the following command:

```
cd existing_repo
git remote add origin https://gitlab.com/leafyseadragon/soundcommandapps.git
git branch -M main
git push -uf origin main
```

## Integrate with your tools

- [ ] [Set up project integrations](https://gitlab.com/-/experiment/new_project_readme_content:35aa787b0e03b0e42725936ae8605de8?https://gitlab.com/leafyseadragon/soundcommandapps/-/settings/integrations)

## Collaborate with your team

- [ ] [Invite team members and collaborators](https://gitlab.com/-/experiment/new_project_readme_content:35aa787b0e03b0e42725936ae8605de8?https://docs.gitlab.com/ee/user/project/members/)
- [ ] [Create a new merge request](https://gitlab.com/-/experiment/new_project_readme_content:35aa787b0e03b0e42725936ae8605de8?https://docs.gitlab.com/ee/user/project/merge_requests/creating_merge_requests.html)
- [ ] [Automatically close issues from merge requests](https://gitlab.com/-/experiment/new_project_readme_content:35aa787b0e03b0e42725936ae8605de8?https://docs.gitlab.com/ee/user/project/issues/managing_issues.html#closing-issues-automatically)
- [ ] [Enable merge request approvals](https://gitlab.com/-/experiment/new_project_readme_content:35aa787b0e03b0e42725936ae8605de8?https://docs.gitlab.com/ee/user/project/merge_requests/approvals/)
- [ ] [Automatically merge when pipeline succeeds](https://gitlab.com/-/experiment/new_project_readme_content:35aa787b0e03b0e42725936ae8605de8?https://docs.gitlab.com/ee/user/project/merge_requests/merge_when_pipeline_succeeds.html)

## Test and Deploy

Use the built-in continuous integration in GitLab.

- [ ] [Get started with GitLab CI/CD](https://gitlab.com/-/experiment/new_project_readme_content:35aa787b0e03b0e42725936ae8605de8?https://docs.gitlab.com/ee/ci/quick_start/index.html)
- [ ] [Analyze your code for known vulnerabilities with Static Application Security Testing(SAST)](https://gitlab.com/-/experiment/new_project_readme_content:35aa787b0e03b0e42725936ae8605de8?https://docs.gitlab.com/ee/user/application_security/sast/)
- [ ] [Deploy to Kubernetes, Amazon EC2, or Amazon ECS using Auto Deploy](https://gitlab.com/-/experiment/new_project_readme_content:35aa787b0e03b0e42725936ae8605de8?https://docs.gitlab.com/ee/topics/autodevops/requirements.html)
- [ ] [Use pull-based deployments for improved Kubernetes management](https://gitlab.com/-/experiment/new_project_readme_content:35aa787b0e03b0e42725936ae8605de8?https://docs.gitlab.com/ee/user/clusters/agent/)
- [ ] [Set up protected environments](https://gitlab.com/-/experiment/new_project_readme_content:35aa787b0e03b0e42725936ae8605de8?https://docs.gitlab.com/ee/ci/environments/protected_environments.html)

***

# Editing this README

When you're ready to make this README your own, just edit this file and use the handy template below (or feel free to structure it however you want - this is just a starting point!).  Thank you to [makeareadme.com](https://www.makeareadme.com) for this template.

## Suggestions for a good README
Every project is different, so consider which of these sections apply to yours. The sections used in the template are suggestions for most open source projects. Also keep in mind that while a README can be too long and detailed, too long is better than too short. If you think your README is too long, consider utilizing another form of documentation rather than cutting out information.

## Name
Choose a self-explaining name for your project.

## Description
Let people know what your project can do specifically. Provide context and add a link to any reference visitors might be unfamiliar with. A list of Features or a Background subsection can also be added here. If there are alternatives to your project, this is a good place to list differentiating factors.

## Badges
On some READMEs, you may see small images that convey metadata, such as whether or not all the tests are passing for the project. You can use Shields to add some to your README. Many services also have instructions for adding a badge.

## Visuals
Depending on what you are making, it can be a good idea to include screenshots or even a video (you'll frequently see GIFs rather than actual videos). Tools like ttygif can help, but check out Asciinema for a more sophisticated method.

## Installation
Within a particular ecosystem, there may be a common way of installing things, such as using Yarn, NuGet, or Homebrew. However, consider the possibility that whoever is reading your README is a novice and would like more guidance. Listing specific steps helps remove ambiguity and gets people to using your project as quickly as possible. If it only runs in a specific context like a particular programming language version or operating system or has dependencies that have to be installed manually, also add a Requirements subsection.

## Usage
Use examples liberally, and show the expected output if you can. It's helpful to have inline the smallest example of usage that you can demonstrate, while providing links to more sophisticated examples if they are too long to reasonably include in the README.

## Support
Tell people where they can go to for help. It can be any combination of an issue tracker, a chat room, an email address, etc.

## Roadmap
If you have ideas for releases in the future, it is a good idea to list them in the README.

## Contributing
State if you are open to contributions and what your requirements are for accepting them.

For people who want to make changes to your project, it's helpful to have some documentation on how to get started. Perhaps there is a script that they should run or some environment variables that they need to set. Make these steps explicit. These instructions could also be useful to your future self.

You can also document commands to lint the code or run tests. These steps help to ensure high code quality and reduce the likelihood that the changes inadvertently break something. Having instructions for running tests is especially helpful if it requires external setup, such as starting a Selenium server for testing in a browser.

## Authors and acknowledgment
Show your appreciation to those who have contributed to the project.

## License
For open source projects, say how it is licensed.

## Project status
If you have run out of energy or time for your project, put a note at the top of the README saying that development has slowed down or stopped completely. Someone may choose to fork your project or volunteer to step in as a maintainer or owner, allowing your project to keep going. You can also make an explicit request for maintainers.

## todo review gitignore file
test git main branch...ok now

-->