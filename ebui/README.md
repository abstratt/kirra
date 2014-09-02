## EBUI is an Email-Based User Interface

EBUI is an Email-Based User Interface. It acts as a front-end to any application that implements a [Kirra-compliant](http://abstratt.github.io/kirra/) REST API.

#### Entity inboxes

Each (top-level) business entity has a corresponding email inbox:

- ticket-\<application\>@\<domain\>...
- expense-\<application\>@\<domain\>...
- todo-\<application\>@\<domain\>...
- ...


#### Instance creation

Whenever an email is sent to one of those entity inboxes, a new instance of that entity is created (if all required information is present in the message). The server replies with either a confirmation email where the from: address corresponds to the instance created, or an error message explaining why creation failed.

#### Replies to the instance creation thread

For childless entities, a user can perform an update to a business entity instance by replying to the email that was sent in response to an instance creation. For entities that aggregate a single kind of child entity that has a required Memo field, responses to the creation email are considered creation of instances of child objects.

#### Email body contents

Email body contents (the email text) map to the first Memo property in the corresponding entity. 

#### Email attachments

Email attachments map to the first Blob property in the corresponding entity. 

#### Setting properties

Properties can be set on creation or update using the following syntax in the body of an email:

    Thanks for your report. I was able to reproduce it locally, and agree it is a 
    dangerous bug. We will fix it right away.
    --
    Priority: High
    Assignee: Jenniffer Strong
    Fix for: v2.1.1


#### Invoking actions

Actions can be explicitly invoked on an object by using the following syntax:

    --
    Done.

which would send the "Done" message to the target object. If the action has parameters, the following syntax can be used:

    --
    Rejected.
    Reason: Expenses on entertainment are not reimbursable.

which would send the "Reject" message with the shown message as an argument for the 'reason' parameter. 

#### A note on sender authentication

*From:* addresses can be easily spoofed. There is no mechanism in EBUI yet for safely authenticating senders, hence it should not be used as-is to perform operations that would require any sort of privilege (authorization control should consider EBUI-originated commands to be from unauthenticated users). Implementation of a proper authentication mechanism is required for any applications where spoofing would not be tolerable and is left as an exercise to the reader.
