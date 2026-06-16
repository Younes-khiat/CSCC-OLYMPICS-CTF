# Challenge Writeup: Fiha?

## Challenge Overview
* **Category:** Web
* **Difficulty:** Easy
* **Objective:** Intercept a web request, identify a blackbox input filtering mechanism, and bypass it using Unicode normalization behavior to retrieve the flag.

---

## Reconnaissance & Initial Analysis

The challenge presents a simple webpage with a button that says **Check**. 

1. Clicking the **Check** button changes the background to red and displays the text `MAFIHACH`.
2. Inspecting the source code or using a local proxy (like Burp Suite) reveals the following front-end behavior when the button is pressed:
   * It sends a `POST` request to `/check`.
   * The content type is `application/x-www-form-urlencoded`.
   * The default payload sent in the body is `fiha=true`.

### Intercepting the Request
Capturing the request in Burp Suite and sending it to Repeater allows us to test the backend behavior dynamically:

* Sending `fiha=true`, `fiha=false`, `fiha=0`, or `fiha=1` returns:
```json
  {"success": false, "note": "Monkey testing lol"}
```
Attempting to send the obvious string fiha=fiha yields a different message:

```json
  {"success": false, "note": "Blocked word"}
```

### Exploitation

To bypass the blacklist, we can use a Unicode ligature. The Latin Small Ligature FI (ﬁ, Unicode \uFB01) is treated as a single character by basic string filters, but decomposes into individual standard ASCII characters f and i when normalized by compatibility forms.

```http
POST /check HTTP/1.1
Host: <CHALLENGE_IP>:5000
Content-Type: application/x-www-form-urlencoded
Content-Length: 11

fiha=ﬁha
```

```json
{
  "success": true,
  "note": "Random testing paid out eventually",
  "flag": "CSCC{f1h4_un1c0de_r38re8f8df88we4!}"
}
```
and you get the flag
