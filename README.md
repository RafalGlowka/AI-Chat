# AI - Chat
Example application build on Gemini AI API. 

!! Project is still in alpha stage and will be improved in the future !!

This time aim is to build something fast, test/prototype functionality and play with it. 
It's more a like a playground without support to all edge cases or perfect architecture. 
It's I hope a first of few project testing AI capabilities in context of Android Apps.

Inspired by OpenAI conference I will start testing by checking how good Gemini-PRO text model is in chatting. 
It will be a very simple app collecting voice recognition data from the microphone and sending them to AI model in a form of chat.
There is only one button. If you hold it recording and voice recognition is working. Then your message is added to the chat with AI model and we wait for answer. 

To make the experience more engaging text to speech module is used to read AI model answers. 

## How run this code
Too run the code you need to get your own gemini API access code and add it to local.properties file in place of xxxxx :
gemini_apiKey=xxxxxxx
openai_apiKey=xxxxxxx

## Known issues
Project is OpenAI ready, but it seems that my free plan do not allow to do any prompts without hitting request limits.

## Video from testing
Video showing chat with gemini pro: https://youtu.be/3kb2IpgolTQ
