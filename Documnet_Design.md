# Project Design Document

## 1. Introduction

### Project Overview

The Chatroom Java Application is a real-time messaging platform for users to communicate. Users can join chatrooms, have private conversations, see who's active, and broadcast messages. The goal is to make it easy for users to connect and share information.

### Problem

We need a simple chat app that supports both group and individual interactions. This project aims to provide a straightforward solution with essential communication features and efficient data management.

## 2. App Description

### Features

Main Menu:

- **Chatrooms:**
  - Create Chatroom: Users can create new chatrooms.
  - Join Chatroom: Users can join existing chatrooms.
- **Direct Message:** Send private messages to another user.
- **Show Active Users:** Display a list of active users.
- **MegaPhone:** Broadcast messages to all active users or specific chatrooms.
- **Exit:** Log out and close the application.

### Problem Solved by the Application

The app solves communication issues by providing:

- **Unified Communication Platform:** Group and private messaging in one app.
- **Real-Time Interaction:** Instant communication with minimal delay.
- **User Engagement:** Active user display and message broadcasting.
- **Scalability and Efficiency:** Efficient data structures for handling multiple users and messages.

## 3. Justification of Choice of Data Structures

Efficient data management is key for a real-time chat app. The chosen data structures balance time and space complexities.

### 3.1. HashMap for User Management

- **Structure:** `HashMap<String, User>`
- **Key:** username (String)
- **Value:** User object with details and status.
- **Rationale:**
  - Fast Access: Efficient for insertion, search, and deletion.
  - Uniqueness: Ensures each username is unique.
- **Time Complexity:**
  - Best case: O(1) for all operations
  - Worst case: O(n) for all operations (rare, in case of hash collisions)
- **Space Complexity:** O(n), where n is the number of users.
- **Note:** We use `java.util.HashMap` instead of `Hashtable` for better performance in our single-threaded application.

### 3.2. HashMap for Chatroom Management

- **Structure:** `HashMap<String, Chatroom>`
- **Key:** chatroomName or ID (String)
- **Value:** Chatroom object with details and participants.
- **Rationale:**
  - Efficient Retrieval: Quick access to chatroom data.
  - Scalability: Handles many chatrooms without significant performance loss.
- **Time Complexity:** Same as User Management HashMap.
- **Space Complexity:** O(m), where m is the number of chatrooms.

### 3.3. ArrayList for Message History

- **Structure:** `ArrayList<Message>` within each Chatroom or DirectMessage object.
- **Rationale:**
  - Ordered Collection: Maintains message sequence.
  - Dynamic Resizing: Adjusts size for varying message numbers.
  - Efficient Indexing: Fast access by index.
- **Time Complexity:**
  - Best case: O(1) for insertion at end and access by index
  - Worst case: O(n) for insertion (when resizing is needed)
- **Space Complexity:** O(n), where n is the number of messages.

### 3.4. HashSet for Active Users

- **Structure:** `HashSet<User>`
- **Rationale:**
  - No Duplicates: Each active user is represented once.
  - Fast Operations: Efficient for insertion, search, and deletion.
- **Time Complexity:**
  - Best case: O(1) for all operations
  - Worst case: O(n) for all operations (rare, in case of hash collisions)
- **Space Complexity:** O(n), where n is the number of active users.

## 4. Interaction and Integration Plan

This section outlines how the app uses the chosen data structures and the general approach for various operations.

### 4.1. User Authentication and Management

#### Interaction with Data Structures:

- **Login Process:**
  - Verify user credentials using the users HashMap.
  - Add the User object to the activeUsers HashSet upon successful login.
- **Logout Process:**
  - Remove the User object from the activeUsers HashSet.

#### General Approach:

- **Authentication:** Check if the username exists in the users HashMap.
- **Adding to Active Users:** Add the User object to the activeUsers HashSet.

### 4.2. Chatroom Creation and Management

#### Interaction with Data Structures:

- **Creating a Chatroom:**
  - Ensure the chatroom name is unique using the chatrooms HashMap.
  - Add a new Chatroom object to the chatrooms HashMap.
- **Joining a Chatroom:**
  - Retrieve the Chatroom object from the chatrooms HashMap.
  - Add the User to the participants list within the Chatroom.

#### General Approach:

- **Create Chatroom:** Check for uniqueness and add the new Chatroom to the HashMap.
- **Join Chatroom:** Retrieve the Chatroom and add the User to its participants list.

### 4.3. Messaging System

#### Interaction with Data Structures:

- **Sending Messages:**
  - Create a Message object with sender, content, and timestamp.
  - Add the Message to the messageHistory ArrayList of the corresponding Chatroom or DirectMessage.
- **Receiving Messages:**
  - Retrieve messages from the messageHistory for display to the user.

#### General Approach:

- **Send Message:** Add the Message object to the messageHistory ArrayList.
- **Retrieve Messages:** Return the messageHistory ArrayList for the Chatroom.

### 4.4. MegaPhone Feature

#### Interaction with Data Structures:

- **Broadcasting Messages:**
  - Send the message to all active users or add it to the messageHistory of all chatrooms.

#### General Approach:

- **Broadcast to All Active Users:** Iterate over activeUsers and send the message.
- **Broadcast to All Chatrooms:** Iterate over chatrooms and add the message to each messageHistory.

### 4.5. Active Users Display

#### Interaction with Data Structures:

Retrieve the list of active users from the activeUsers HashSet for display.

#### General Approach:

- **Display Active Users:** Collect usernames from activeUsers and return them as a list.

## Integration Summary

The integration of these data structures ensures:

- **Efficiency:** Operations are fast, providing a responsive user experience.
- **Scalability:** The app can handle more users and messages without performance loss.
- **Data Integrity:** Proper management of user sessions and chatrooms prevents data issues.
