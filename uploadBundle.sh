#!/bin/bash
function getJwt {
    if (($# != 3)); then echo "Please pass the auth server, auth email, and auth key!"; exit 2; fi
    AUTH_SERVER=$1
    AUTH_EMAIL=$2
    AUTH_KEY=$3

    # Create JWT header
    JWT_HEADER=$(echo -n '{"alg":"RS256","typ":"JWT"}' | openssl base64 -e)

    # Create JWT body
    JWT_BODY=$(cat <<EOF
    {
        "aud": "${AUTH_SERVER}",
        "iss": "${AUTH_EMAIL}",
        "scope": "https://www.googleapis.com/auth/androidpublisher",
        "exp": $(($(date +%s)+300)),
        "iat": $(date +%s)
    }
EOF
    )
    JWT_BODY_CLEAN=$(echo -n "$JWT_BODY" | openssl base64 -e)

    # Create complete payload
    JWT_PAYLOAD=$(echo -n "$JWT_HEADER.$JWT_BODY_CLEAN" | tr -d '\n' | tr -d '=' | tr '/+' '_-')

    # Create JWT signature
    JWT_SIGNATURE=$(echo -n "$JWT_PAYLOAD" | openssl dgst -binary -sha256 -sign <(printf '%s\n' "$AUTH_KEY") | openssl base64 -e)
    JWT_SIGNATURE_CLEAN=$(echo -n "$JWT_SIGNATURE" | tr -d '\n' | tr -d '=' | tr '/+' '_-')

    # Combine JWT payload and signature
    echo ${JWT_PAYLOAD}.${JWT_SIGNATURE_CLEAN}
}

function getAccessToken {
    if (($# != 2)); then echo "Please pass the auth server and JWT!"; exit 2; fi
    AUTH_SERVER=$1
    JWT=$2

    # Send JWT to auth server
    HTTP_RESPONSE=$(curl --silent --write-out "HTTPSTATUS:%{http_code}" \
      --header "Content-type: application/x-www-form-urlencoded" \
      --request POST \
      --data "grant_type=urn%3Aietf%3Aparams%3Aoauth%3Agrant-type%3Ajwt-bearer&assertion=$JWT" \
      "$AUTH_SERVER")

    # Parse auth server response for body and status
    HTTP_BODY=$(echo ${HTTP_RESPONSE} | sed -e 's/HTTPSTATUS\:.*//g')
    HTTP_STATUS=$(echo ${HTTP_RESPONSE} | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')

    # Check response status for success, and retrieve access token if possible
    if [[ ${HTTP_STATUS} != 200 ]]; then
        echo -e "Create access token failed.\nStatus: $HTTP_STATUS\nBody: $HTTP_BODY\nExiting."
        exit 1
    fi
    echo $(echo ${HTTP_BODY} | jq -r '.access_token')
}

function uploadBundle {
    if (($# != 2)); then echo "Please pass the access token and bundle location!"; exit 2; fi
    ACCESS_TOKEN=$1
    BUNDLE_LOCATION=$2

    # Send app bundle and access token to internal app sharing
    PACKAGE="com.vmenon.mpo"
    HTTP_RESPONSE=$(curl --write-out "HTTPSTATUS:%{http_code}" \
      --header "Authorization: Bearer $ACCESS_TOKEN" \
      --header "Content-Type: application/octet-stream" \
      --progress-bar \
      --request POST \
      --upload-file ${BUNDLE_LOCATION} \
      https://www.googleapis.com/upload/androidpublisher/v3/applications/internalappsharing/${PACKAGE}/artifacts/bundle?uploadType=media)

    # Parse internal app sharing response for body and status
    HTTP_BODY=$(echo ${HTTP_RESPONSE} | sed -e 's/HTTPSTATUS\:.*//g')
    HTTP_STATUS=$(echo ${HTTP_RESPONSE} | tr -d '\n' | sed -e 's/.*HTTPSTATUS://')

   # Check response status for success, and retrieve download URL if possible
   if [[ ${HTTP_STATUS} != 200 ]]; then
       echo -e "Upload app bundle failed.\nStatus: $HTTP_STATUS\nBody: $HTTP_BODY\nExiting."
       exit 1
   fi
   echo $(echo ${HTTP_BODY} | jq -r '.downloadUrl')
}

function sendNotification {
  DOWNLOAD_URL=$1
  TIMESTAMP=$(date -u +%FT%TZ)
  curl -d "from=$NOTIFICATION_SENDER&to=$NOTIFICATION_RECIPIENT&subject=New Build Available: $TRAVIS_BUILD_NUMBER&text=New build available ($TIMESTAMP): $DOWNLOAD_URL" -X POST "$NOTIFICATION_API_URL"
}

set -e

# Parse service account JSON for authentication information
AUTH_SERVER=$(jq -r '.token_uri' "${SERVICE_ACCOUNT_FILE}")
AUTH_EMAIL=$(jq -r '.client_email' "${SERVICE_ACCOUNT_FILE}")
AUTH_KEY=$(jq -r '.private_key' "${SERVICE_ACCOUNT_FILE}")
echo "Retrieved service account from JSON!"

# Generate JWT from authentication information
JWT=$(getJwt "$AUTH_SERVER" "$AUTH_EMAIL" "$AUTH_KEY")
echo "Generated JWT!"

# Use JWT to authenticate with Google and retrieve an access token
ACCESS_TOKEN=$(getAccessToken "$AUTH_SERVER" "$JWT")
echo "Generated access token!"

# Use access token to upload app bundle to Google Play Internal App Sharing
URL=$(uploadBundle "$ACCESS_TOKEN" "$BUNDLE_LOCATION")
echo "Uploaded app bundle to ${URL}"

sendNotification "$URL"