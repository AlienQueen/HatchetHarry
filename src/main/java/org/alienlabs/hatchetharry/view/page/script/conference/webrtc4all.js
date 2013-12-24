/*
* Copyright (C) 2012 Doubango Telecom <http://www.doubango.org>
* License: BSD
* This file is part of Open Source sipML5 solution <http://www.sipml5.org>
*/
// "http://tools.ietf.org/html/draft-uberti-rtcweb-jsep-02" implementation for Safari, Opera, Firefox and IE

w4aPeerConnection.prototype.s_configuration = null;
w4aPeerConnection.prototype.f_IceCallback = null;
w4aPeerConnection.prototype.o_peer = null;
w4aPeerConnection.prototype.localDescription = null; // part of the standard
w4aPeerConnection.prototype.remoteDescription = null; // part of the standard

w4aSessionDescription.prototype.o_sdp = null;

w4aIceCandidate.prototype.media = null;
w4aIceCandidate.prototype.label = null; // part of the standard

var __o_roap_stream = null;
var __o_jsep_stream_audio = null;
var __o_jsep_stream_audiovideo = null;

var WebRtcType_e =
{
    NONE: -1,

    NATIVE: 0,
    IE: 1,
    NPAPI: 2,
    ERICSSON: 3
};

var __webrtc_type = WebRtcType_e.NONE;
var __b_webrtc4all_initialized = false;
var __b_webrtc4ie_peerconn = undefined;
function WebRtc4all_Init() {
    if (!__b_webrtc4all_initialized) {
        try {
            // NPAPI plugin object
            var oWebRtc4npapi = document.createElement('embed');
            oWebRtc4npapi.id = "WebRtc4npapi";
            oWebRtc4npapi.type = "application/w4a";
            oWebRtc4npapi.width = oWebRtc4npapi.height = '1px';
            oWebRtc4npapi.stype = 'visibility:hidden;';
            document.body.appendChild(oWebRtc4npapi);
        }
        catch (e) { }        

        // WebRtc plugin type
        try {
            window.nativeRTCPeerConnection = (window.webkitPeerConnection00 || window.webkitRTCPeerConnection || window.mozRTCPeerConnection);
            window.nativeRTCSessionDescription = (window.mozRTCSessionDescription || window.RTCSessionDescription); // order is very important: "RTCSessionDescription" defined in Nighly but useless
            window.nativeRTCIceCandidate = (window.mozRTCIceCandidate || window.RTCIceCandidate);
            window.nativeURL = (window.webkitURL || window.URL);
            navigator.nativeGetUserMedia = (navigator.webkitGetUserMedia || navigator.mozGetUserMedia);
            if ((navigator.nativeGetUserMedia && window.nativeRTCPeerConnection)) {
                __webrtc_type = WebRtcType_e.NATIVE; // Google Chrome
            }
            else if (navigator.nativeGetUserMedia && window.webkitPeerConnection) {
                __webrtc_type = WebRtcType_e.ERICSSON;
            }
        }
        catch (e) { }
        if (__webrtc_type == WebRtcType_e.NONE) {
            try {
                if ((__b_webrtc4ie_peerconn = new ActiveXObject("webrtc4ie.PeerConnection"))) {
                    __webrtc_type = WebRtcType_e.IE; // Internet Explorer
                }
            }
            catch (e) {
                if (WebRtc4npapi.supportsPeerConnection) {
                    __webrtc_type = WebRtcType_e.NPAPI; // Opera, Firefox or Safari
                }
            }
        }

        __b_webrtc4all_initialized = true;

        if (navigator.nativeGetUserMedia && WebRtc4all_GetType() == WebRtcType_e.ERICSSON) {
            navigator.nativeGetUserMedia("audio, video",
                    function (stream) {
                        tsk_utils_log_info("Got stream :)");
                        __o_roap_stream = stream;
                    },
                    function (error) {
                        tsk_utils_log_error(error);
                    });
        }
    }
}

function WebRtc4all_GetVersion() {
    try {
        if (__webrtc_type == WebRtcType_e.IE) {
            return __b_webrtc4ie_peerconn.version;
        }
        else if (__webrtc_type == WebRtcType_e.NPAPI) {
            return WebRtc4npapi.version;
        }
    }
     catch (e) { }
     return "0.0.0.0";
}

function WebRtc4all_GetType() {
    return __webrtc_type;
}

var __looper = undefined;
function WebRtc4all_GetLooper() {
    if (__looper == undefined && tsk_utils_have_webrtc4ie()) {
        try {
            var oLooper = document.createElement('object');
            oLooper.classid = "clsid:7082C446-54A8-4280-A18D-54143846211A";
            oLooper.width = oLooper.height = '1px';
            document.body.appendChild(oLooper);
            if (!(__looper = oLooper.hWnd)) {
                tsk_utils_log_error("Failed to create looper");
            }
        }
        catch (e) {
            tsk_utils_log_error(e);
            __looper = null;
        }
    }
    return __looper;
}

function WebRtc4all_SetDisplays(o_local_elt, o_remote_elt) {
    if (__webrtc_type == WebRtcType_e.IE) {
        // visiblity must be "visible"  for the first time to force handle creation
        if (o_local_elt) {
            o_local_elt.innerHTML = "<object id=\"__o_display_local\" classid=\"clsid:5C2C407B-09D9-449B-BB83-C39B7802A684\"" +
                                    " class=\"video\" width=\"88px\" height=\"72px\" style=\"margin-top: -80px; margin-left: 5px; background-color: #000000; visibility:visible\"> </object>";
            __o_display_local.style.visibility = "hidden";
        }
        if (o_remote_elt) {
            o_remote_elt.innerHTML = "<object id=\"__o_display_remote\" classid=\"clsid:5C2C407B-09D9-449B-BB83-C39B7802A684\"" +
                                     " width=\"100%\" height=\"100%\" style=\"visibility:visible;\"> </object>";
            __o_display_remote.style.visibility = "hidden";
        }
    }
    else if (__webrtc_type == WebRtcType_e.NPAPI) {
        if (o_local_elt) {
            o_local_elt.innerHTML = "<embed id=\"__o_display_local\" type=\"application/w4a-display\"" +
                                    " class=\"video\" width=\"88px\" height=\"72px\" style=\"margin-top: -80px; margin-left: 5px; background-color: #000000; visibility:hidden\"> </embed>";
        }
        if (o_remote_elt) {
            o_remote_elt.innerHTML = "<embed id=\"__o_display_remote\" type=\"application/w4a-display\"" +
                                     " width=\"100%\" height=\"100%\" style=\"visibility:hidden;\"> </embed>";
        }
    }
}

function w4aSessionDescription(s_sdp) {
    if (!__b_webrtc4all_initialized) {
        WebRtc4all_Init();
    }
    var b_isInternetExplorer = (__webrtc_type == WebRtcType_e.IE);
    this.o_sdp = b_isInternetExplorer ? new ActiveXObject("webrtc4ie.SessionDescription") : WebRtc4npapi.createSessionDescription();
    this.o_sdp.Init(s_sdp ? (s_sdp + "") : null);
}

w4aSessionDescription.prototype.toSdp = function () {
    return this.o_sdp.toSdp();
}
w4aSessionDescription.prototype.toString = w4aSessionDescription.prototype.toSdp;

w4aSessionDescription.prototype.addCandidate = function (o_candidate) {
    if(o_candidate && o_candidate.media && o_candidate.label) {
        this.o_sdp.addCandidate(o_candidate.media, o_candidate.label);
    }
}

function w4aIceCandidate(media, label) {
    this.media = media;
    this.label = label;
}

w4aIceCandidate.prototype.toSdp = function () {
    return this.label;
};

function w4aPeerConnection(s_configuration, f_IceCallback) {
    if (!__b_webrtc4all_initialized) {
        WebRtc4all_Init();
    }
    var This = this;
    var b_isInternetExplorer = (__webrtc_type == WebRtcType_e.IE);
    this.s_configuration = s_configuration;
    this.f_IceCallback = f_IceCallback;
    this.o_peer = b_isInternetExplorer ? new ActiveXObject("webrtc4ie.PeerConnection") : WebRtc4npapi.createPeerConnection();
    this.o_peer.Init(s_configuration);

    // attach displays if defined by the user
    try { this.o_peer.localVideo = (window.__o_display_local ? window.__o_display_local.hWnd : 0); } catch (e) { }
    try { this.o_peer.remoteVideo = (window.__o_display_remote ? window.__o_display_remote.hWnd : 0); } catch (e) { }

    // register callback function
    if (b_isInternetExplorer) {
        eval("function This.o_peer::IceCallback(media, label, bMoreToFollow) { return This.onIceCallback (media, label, bMoreToFollow); }");
    }
    else {
        this.o_peer.opaque = This;
        this.o_peer.setCallbackFuncName("w4aPeerConnection_NPAPI_OnEvent");
    }
};


// actions, for setLocalDescription/setRemoteDescription
w4aPeerConnection.SDP_OFFER = 0x100;
w4aPeerConnection.SDP_PRANSWER = 0x200;
w4aPeerConnection.SDP_ANSWER = 0x300;

// PeerConnection state
w4aPeerConnection.NEW = 0;     // initial state
w4aPeerConnection.OPENING = 1; // local or remote desc set
w4aPeerConnection.ACTIVE = 2;  // local and remote desc set
w4aPeerConnection.CLOSED = 3;  // ended state

// ICE state
w4aPeerConnection.ICE_GATHERING = 0x100;
w4aPeerConnection.ICE_WAITING = 0x200;
w4aPeerConnection.ICE_CHECKING = 0x300;
w4aPeerConnection.ICE_CONNECTED = 0x400;
w4aPeerConnection.ICE_COMPLETED = 0x500;
w4aPeerConnection.ICE_FAILED = 0x600;
w4aPeerConnection.ICE_CLOSED = 0x700;

// SessionDescription createOffer (MediaHints hints)
w4aPeerConnection.prototype.createOffer = function (o_hints) {
    if ((__webrtc_type == WebRtcType_e.IE)) {
        return new w4aSessionDescription(this.o_peer.createOffer(o_hints.has_audio, o_hints.has_video));
    }
    else {
        var oSdp = this.o_peer.createOffer(o_hints.has_audio, o_hints.has_video);
        if (oSdp) {
            return new w4aSessionDescription(oSdp.toSdp());
        }
        return null;
    }
}

// SessionDescription createAnswer (DOMString offer, MediaHints hints);
w4aPeerConnection.prototype.createAnswer = function (s_offer, o_hints) {
    if ((__webrtc_type == WebRtcType_e.IE)) {
        return new w4aSessionDescription(this.o_peer.createAnswer(o_hints.has_audio, o_hints.has_video));
    }
    else {
        var oSdp = this.o_peer.createAnswer(o_hints.has_audio, o_hints.has_video);
        if (oSdp) {
            return new w4aSessionDescription(oSdp.toSdp());
        }
        return null;
    }
}

// void setLocalDescription (unsigned short action, SessionDescription desc);
w4aPeerConnection.prototype.setLocalDescription = function (i_action, o_desc) {
    this.o_peer.setLocalDescription(i_action, (__webrtc_type == WebRtcType_e.IE) ? o_desc.toSdp() : o_desc.o_sdp);
    this.localDescription = new w4aSessionDescription(this.o_peer.localDescription);
}

// void setRemoteDescription (unsigned short action, SessionDescription desc);
w4aPeerConnection.prototype.setRemoteDescription = function (i_action, o_desc) {
    this.o_peer.setRemoteDescription(i_action, (__webrtc_type == WebRtcType_e.IE) ? o_desc.toSdp() : o_desc.o_sdp);
    this.remoteDescription = new w4aSessionDescription(this.o_peer.remoteDescription);
}

// void startIce (optional IceOptions options);
w4aPeerConnection.prototype.startIce = function (o_options) {
    this.o_peer.startIce(0/* all */, WebRtc4all_GetLooper());
}

// void startMedia (void);
// Not part of the specification
// In native WebRTC, media is started when ICE negotiation complete. For WebRTC4all we cannot rely on ICE as it's optional.
w4aPeerConnection.prototype.startMedia = function (o_options) {
    if(this.o_peer /*&& this.o_peer.startMedia*/) {
        // startMedia() introduced when ICE become optional. "if(this.o_peer.startMedia)" always returns false on IE.
        try { this.o_peer.startMedia(); }
        catch (e) { }
    }
}

// void processIceMessage (IceCandidate candidate);
w4aPeerConnection.prototype.processIceMessage = function (o_candidate) {
    tsk_utils_log_error("Not implemented"); // we expect all ICE candidates in the SDP offer (SIP)
}

// void addStream (MediaStream stream, MediaStreamHints hints);
w4aPeerConnection.prototype.addStream = function (o_stream, o_hints) {
}

// void removeStream (MediaStream stream);
w4aPeerConnection.prototype.removeStream = function (o_stream) {
}

// void close()
w4aPeerConnection.prototype.close = function () {
    if (this.o_peer) {
        this.o_peer.close();
    }
}

w4aPeerConnection.prototype.onIceCallback = function (media, label, bMoreToFollow) {
    tsk_utils_log_info("w4aPeerConnection::onIceCallback(" + media + "," + label + "," + bMoreToFollow + ")");
    this.iceState = this.o_peer.iceState;
    if (this.f_IceCallback) {
        this.f_IceCallback(new w4aIceCandidate(media, label), bMoreToFollow);
    }
}


function w4aPeerConnection_NPAPI_OnEvent(o_peer, sMedia, sLabel, bMoreToFollow) {
    o_peer.onIceCallback(sMedia, sLabel, bMoreToFollow);
}


//Visio-conference
jQuery(function() {
	jQuery("#conference").dialog({ autoOpen: false, position: { my: "center", at: "center" } });
});

// to avoid caching
    //if (window.location.href.indexOf("svn=") == -1) {
    //    window.location.href += (window.location.href.indexOf("?") == -1 ? "?svn=13" : "&svn=13");
    //}

    var sTransferNumber;
    var oRingTone, oRingbackTone;
    var oSipStack, oSipSessionRegister, oSipSessionCall, oSipSessionTransferCall;
    var videoRemote, videoLocal, audioRemote;
    var bFullScreen = false;
    var oNotifICall;
    var oReadyStateTimer;
    var bDisableVideo = false;
    var viewVideoLocal, viewVideoRemote; // <video> (webrtc) or <div> (webrtc4all)
    var oConfigCall;

    C = 
    {
        divKeyPadWidth: 220
    };

    window.onload = function () {
        if(window.console) {
            window.console.info("location=" + window.location);
        }
        videoLocal = document.getElementById("video_local");
        videoRemote = document.getElementById("video_remote");
        audioRemote = document.getElementById("audio_remote");

        document.onkeyup = onKeyUp;
        document.body.onkeyup = onKeyUp;
        //divCallCtrl.onmousemove = onDivCallCtrlMouseMove;

        // set debug level
        SIPml.setDebugLevel((window.localStorage && window.localStorage.getItem('org.doubango.expert.disable_debug') == "true") ? "error" : "info");

        loadCredentials();
        loadCallOptions();

        // Initialize call button
        uiBtnCallSetText("Call");

        oReadyStateTimer = setInterval(function () {
            if (document.readyState === "complete") {
                clearInterval(oReadyStateTimer);
                // initialize SIPML5
                SIPml.init(postInit);
            }
        },
        500);
    };

    function postInit() {
        // check webrtc4all version
        if (SIPml.isWebRtc4AllSupported() && SIPml.isWebRtc4AllPluginOutdated()) {            
            if (confirm("Your WebRtc4all extension is outdated ("+SIPml.getWebRtc4AllVersion()+"). A new version with critical bug fix is available. Do you want to install it?\nIMPORTANT: You must restart your browser after the installation.")) {
                window.location = 'http://code.google.com/p/webrtc4all/downloads/list';
                return;
            }
        }

        // check for WebRTC support
        if (!SIPml.isWebRtcSupported()) {
            // is it chrome?
            if (SIPml.getNavigatorFriendlyName() == 'chrome') {
                    if (confirm("You're using an old Chrome version or WebRTC is not enabled.\nDo you want to see how to enable WebRTC?")) {
                        window.location = 'http://www.webrtc.org/running-the-demos';
                    }
                    else {
                        window.location = "index.html";
                    }
                    return;
            }
                
            // for now the plugins (WebRTC4all only works on Windows)
            if (SIPml.getSystemFriendlyName() == 'windows') {
                // Internet explorer
                if (SIPml.getNavigatorFriendlyName() == 'ie') {
                    // Check for IE version 
                    if (parseFloat(SIPml.getNavigatorVersion()) < 9.0) {
                        if (confirm("You are using an old IE version. You need at least version 9. Would you like to update IE?")) {
                            window.location = 'http://windows.microsoft.com/en-us/internet-explorer/products/ie/home';
                        }
                        else {
                            window.location = "index.html";
                        }
                    }

                    // check for WebRTC4all extension
                    if (!SIPml.isWebRtc4AllSupported()) {
                        if (confirm("webrtc4all extension is not installed. Do you want to install it?\nIMPORTANT: You must restart your browser after the installation.")) {
                            window.location = 'http://code.google.com/p/webrtc4all/downloads/list';
                        }
                        else {
                            // Must do nothing: give the user the chance to accept the extension
                            // window.location = "index.html";
                        }
                    }
                    // break page loading ('window.location' won't stop JS execution)
                    if (!SIPml.isWebRtc4AllSupported()) {
                        return;
                    }
                }
                else if (SIPml.getNavigatorFriendlyName() == "safari" || SIPml.getNavigatorFriendlyName() == "firefox" || SIPml.getNavigatorFriendlyName() == "opera") {
                    if (confirm("Your browser don't support WebRTC.\nDo you want to install WebRTC4all extension to enjoy audio/video calls?\nIMPORTANT: You must restart your browser after the installation.")) {
                        window.location = 'http://code.google.com/p/webrtc4all/downloads/list';
                    }
                    else {
                        window.location = "index.html";
                    }
                    return;
                }
            }
            // OSX, Unix, Android, iOS...
            else {
                if (confirm('WebRTC not supported on your browser.\nDo you want to download a WebRTC-capable browser?')) {
                    window.location = 'https://www.google.com/intl/en/chrome/browser/';
                }
                else {
                    window.location = "index.html";
                }
                return;
            }
        }

        // checks for WebSocket support
        if (!SIPml.isWebSocketSupported() && !SIPml.isWebRtc4AllSupported()) {
            if (confirm('Your browser don\'t support WebSockets.\nDo you want to download a WebSocket-capable browser?')) {
                window.location = 'https://www.google.com/intl/en/chrome/browser/';
            }
            else {
                window.location = "index.html";
            }
            return;
        }

        // FIXME: displays must be per session

        // attachs video displays
        if (SIPml.isWebRtc4AllSupported()) {
            viewVideoLocal = document.getElementById("divVideoLocal");
            viewVideoRemote = document.getElementById("divVideoRemote");
            WebRtc4all_SetDisplays(viewVideoLocal, viewVideoRemote); // FIXME: move to SIPml.* API
        }
        else{
            viewVideoLocal = videoLocal;
            viewVideoRemote = videoRemote;
        }

        if (!SIPml.isWebRtc4AllSupported() && !SIPml.isWebRtcSupported()) {
            if (confirm('Your browser don\'t support WebRTC.\naudio/video calls will be disabled.\nDo you want to download a WebRTC-capable browser?')) {
                window.location = 'https://www.google.com/intl/en/chrome/browser/';
            }
        }
        
        btnRegister.disabled = false;
        document.body.style.cursor = 'default';
        oConfigCall = {
            audio_remote: audioRemote,
            video_local: viewVideoLocal,
            video_remote: viewVideoRemote,
            bandwidth: { audio:undefined, video:undefined },
            video_size: { minWidth:undefined, minHeight:undefined, maxWidth:undefined, maxHeight:undefined },
            events_listener: { events: '*', listener: onSipEventSession },
            sip_caps: [
                            { name: '+g.oma.sip-im' },
                            { name: '+sip.ice' },
                            { name: 'language', value: '\"en,fr\"' }
                        ]
        };
    }


    function loadCallOptions() {
        if (window.localStorage) {
            var s_value;
            if ((s_value = window.localStorage.getItem('org.doubango.call.phone_number'))) txtPhoneNumber.value = s_value;
            bDisableVideo = (window.localStorage.getItem('org.doubango.expert.disable_video') == "true");

            txtCallStatus.innerHTML = '<i>Video ' + (bDisableVideo ? 'disabled' : 'enabled') + '</i>';
        }
    }

    function saveCallOptions() {
        if (window.localStorage) {
            window.localStorage.setItem('org.doubango.call.phone_number', txtPhoneNumber.value);
            window.localStorage.setItem('org.doubango.expert.disable_video', bDisableVideo ? "true" : "false");
        }
    }

    function loadCredentials() {
        if (window.localStorage) {
            // IE retuns 'null' if not defined
            var s_value;
            if ((s_value = window.localStorage.getItem('org.doubango.identity.display_name'))) txtDisplayName.value = s_value;
            if ((s_value = window.localStorage.getItem('org.doubango.identity.impi'))) txtPrivateIdentity.value = s_value;
            if ((s_value = window.localStorage.getItem('org.doubango.identity.impu'))) txtPublicIdentity.value = s_value;
            if ((s_value = window.localStorage.getItem('org.doubango.identity.password'))) txtPassword.value = s_value;
            if ((s_value = window.localStorage.getItem('org.doubango.identity.realm'))) txtRealm.value = s_value;
        }
        else {
            /*txtDisplayName.value = "005";
            txtPrivateIdentity.value = "005";
            txtPublicIdentity.value = "sip:005@192.168.0.28";
            txtPassword.value = "005";
            txtRealm.value = "192.168.0.28";
            txtPhoneNumber.value = "005";*/
        }
    };

    function saveCredentials() {
        if (window.localStorage) {
            window.localStorage.setItem('org.doubango.identity.display_name', txtDisplayName.value);
            window.localStorage.setItem('org.doubango.identity.impi', txtPrivateIdentity.value);
            window.localStorage.setItem('org.doubango.identity.impu', txtPublicIdentity.value);
            window.localStorage.setItem('org.doubango.identity.password', txtPassword.value);
            window.localStorage.setItem('org.doubango.identity.realm', txtRealm.value);
        }
    };

    // sends SIP REGISTER request to login
    function sipRegister() {
        // catch exception for IE (DOM not ready)
        try {
            btnRegister.disabled = true;
            if (!txtRealm.value || !txtPrivateIdentity.value || !txtPublicIdentity.value) {
                txtRegStatus.innerHTML = '<b>Please fill madatory fields (*)</b>';
                btnRegister.disabled = false;
                return;
            }
            var o_impu = tsip_uri.prototype.Parse(txtPublicIdentity.value);
            if (!o_impu || !o_impu.s_user_name || !o_impu.s_host) {
                txtRegStatus.innerHTML = "<b>[" + txtPublicIdentity.value + "] is not a valid Public identity</b>";
                btnRegister.disabled = false;
                return;
            }

            // enable notifications if not already done
            if (window.webkitNotifications && window.webkitNotifications.checkPermission() != 0) {
                window.webkitNotifications.requestPermission();
            }

            // save credentials
            saveCredentials();

            // update debug level to be sure new values will be used if the user haven't updated the page
            SIPml.setDebugLevel((window.localStorage && window.localStorage.getItem('org.doubango.expert.disable_debug') == "true") ? "error" : "info");

            // create SIP stack
            oSipStack = new SIPml.Stack({
                    realm: txtRealm.value,
                    impi: txtPrivateIdentity.value,
                    impu: txtPublicIdentity.value,
                    password: txtPassword.value,
                    display_name: txtDisplayName.value,
                    websocket_proxy_url: (window.localStorage ? window.localStorage.getItem('org.doubango.expert.websocket_server_url') : null),
                    outbound_proxy_url: (window.localStorage ? window.localStorage.getItem('org.doubango.expert.sip_outboundproxy_url') : null),
                    ice_servers: (window.localStorage ? window.localStorage.getItem('org.doubango.expert.ice_servers') : null),
                    enable_rtcweb_breaker: (window.localStorage ? window.localStorage.getItem('org.doubango.expert.enable_rtcweb_breaker') == "true" : false),
                    events_listener: { events: '*', listener: onSipEventStack },
                    enable_early_ims: (window.localStorage ? window.localStorage.getItem('org.doubango.expert.disable_early_ims') != "true" : true), // Must be true unless you're using a real IMS network
                    enable_media_stream_cache: (window.localStorage ? window.localStorage.getItem('org.doubango.expert.enable_media_caching') == "true" : false),
                    bandwidth: (window.localStorage ? tsk_string_to_object(window.localStorage.getItem('org.doubango.expert.bandwidth')) : null), // could be redefined a session-level
                    video_size: (window.localStorage ? tsk_string_to_object(window.localStorage.getItem('org.doubango.expert.video_size')) : null), // could be redefined a session-level
                    sip_headers: [
                            { name: 'User-Agent', value: 'IM-client/OMA1.0 sipML5-v1.2013.08.10B' },
                            { name: 'Organization', value: 'Doubango Telecom' }
                    ]
                }
            );
            if (oSipStack.start() != 0) {
                txtRegStatus.innerHTML = '<b>Failed to start the SIP stack</b>';
            }
            else return;
        }
        catch (e) {
            txtRegStatus.innerHTML = "<b>2:" + e + "</b>";
        }
        btnRegister.disabled = false;
    }

    // sends SIP REGISTER (expires=0) to logout
    function sipUnRegister() {
        if (oSipStack) {
            oSipStack.stop(); // shutdown all sessions
        }
    }

    // makes a call (SIP INVITE)
    function sipCall(s_type) {
        if (oSipStack && !oSipSessionCall && !tsk_string_is_null_or_empty(txtPhoneNumber.value)) {
            if(s_type == 'call-screenshare') {
                if(!SIPml.isScreenShareSupported()) {
                    alert('Screen sharing not supported. Are you using chrome 26+?');
                    return;
                }
                if (!location.protocol.match('https')){
                    if (confirm("Screen sharing requires https://. Do you want to be redirected?")) {
                        sipUnRegister();
                        window.location = 'https://ns313841.ovh.net/call.htm';
                    }
                    return;
                }
            }
            btnCall.disabled = true;
            btnHangUp.disabled = false;

            if(window.localStorage) {
                oConfigCall.bandwidth = tsk_string_to_object(window.localStorage.getItem('org.doubango.expert.bandwidth')); // already defined at stack-level but redifined to use latest values
                oConfigCall.video_size = tsk_string_to_object(window.localStorage.getItem('org.doubango.expert.video_size')); // already defined at stack-level but redifined to use latest values
            }
            
            // create call session
            oSipSessionCall = oSipStack.newSession(s_type, oConfigCall);
            // make call
            if (oSipSessionCall.call(txtPhoneNumber.value) != 0) {
                oSipSessionCall = null;
                txtCallStatus.value = 'Failed to make call';
                btnCall.disabled = false;
                btnHangUp.disabled = true;
                return;
            }
            saveCallOptions();
        }
        else if (oSipSessionCall) {
            txtCallStatus.innerHTML = '<i>Connecting...</i>';
            oSipSessionCall.accept(oConfigCall);
        }
    }

    // transfers the call
    function sipTransfer() {
        if (oSipSessionCall) {
            var s_destination = prompt('Enter destination number', '');
            if (!tsk_string_is_null_or_empty(s_destination)) {
                btnTransfer.disabled = true;
                if (oSipSessionCall.transfer(s_destination) != 0) {
                    txtCallStatus.innerHTML = '<i>Call transfer failed</i>';
                    btnTransfer.disabled = false;
                    return;
                }
                txtCallStatus.innerHTML = '<i>Transfering the call...</i>';
            }
        }
    }
    
    // holds or resumes the call
    function sipToggleHoldResume() {
        if (oSipSessionCall) {
            var i_ret;
            btnHoldResume.disabled = true;
            txtCallStatus.innerHTML = oSipSessionCall.bHeld ? '<i>Resuming the call...</i>' : '<i>Holding the call...</i>';
            i_ret = oSipSessionCall.bHeld ? oSipSessionCall.resume() : oSipSessionCall.hold();
            if (i_ret != 0) {
                txtCallStatus.innerHTML = '<i>Hold / Resume failed</i>';
                btnHoldResume.disabled = false;
                return;
            }
        }
    }

    // terminates the call (SIP BYE or CANCEL)
    function sipHangUp() {
        if (oSipSessionCall) {
            txtCallStatus.innerHTML = '<i>Terminating the call...</i>';
            oSipSessionCall.hangup({events_listener: { events: '*', listener: onSipEventSession }});
        }
    }

    function sipSendDTMF(c){
        if(oSipSessionCall && c){
            if(oSipSessionCall.dtmf(c) == 0){
                try { dtmfTone.play(); } catch(e){ }
            }
        }
    }

    function startRingTone() {
        try { ringtone.play(); }
        catch (e) { }
    }

    function stopRingTone() {
        try { ringtone.pause(); }
        catch (e) { }
    }

    function startRingbackTone() {
        try { ringbacktone.play(); }
        catch (e) { }
    }

    function stopRingbackTone() {
        try { ringbacktone.pause(); }
        catch (e) { }
    }

    function toggleFullScreen() {
        if (videoRemote.webkitSupportsFullscreen) {
            fullScreen(!videoRemote.webkitDisplayingFullscreen);
        }
        else {
            fullScreen(!bFullScreen);
        }
    }

    function openKeyPad(){
        divKeyPad.style.visibility = 'visible';
        divKeyPad.style.left = ((document.body.clientWidth - C.divKeyPadWidth) >> 1) + 'px';
        divKeyPad.style.top = '70px';
        divGlassPanel.style.visibility = 'visible';
    }

    function closeKeyPad(){
        divKeyPad.style.left = '0px';
        divKeyPad.style.top = '0px';
        divKeyPad.style.visibility = 'hidden';
        divGlassPanel.style.visibility = 'hidden';
    }

    function fullScreen(b_fs) {
        bFullScreen = b_fs;
        if (tsk_utils_have_webrtc4native() && bFullScreen && videoRemote.webkitSupportsFullscreen) {
            if (bFullScreen) {
                videoRemote.webkitEnterFullScreen();
            }
            else {
                videoRemote.webkitExitFullscreen();
            }
        }
        else {
            if (tsk_utils_have_webrtc4npapi()) {
                try { if(window.__o_display_remote) window.__o_display_remote.setFullScreen(b_fs); }
                catch (e) { divVideo.setAttribute("class", b_fs ? "full-screen" : "normal-screen"); }
            }
            else {
                divVideo.setAttribute("class", b_fs ? "full-screen" : "normal-screen");
            }
        }
    }

    function showNotifICall(s_number) {
        // permission already asked when we registered
        if (window.webkitNotifications && window.webkitNotifications.checkPermission() == 0) {
            if (oNotifICall) {
                oNotifICall.cancel();
            }
            oNotifICall = window.webkitNotifications.createNotification('images/sipml-34x39.png', 'Incaming call', 'Incoming call from ' + s_number);
            oNotifICall.onclose = function () { oNotifICall = null; };
            oNotifICall.show();
        }
    }

    function onKeyUp(evt) {
        evt = (evt || window.event);
        if (evt.keyCode == 27) {
            fullScreen(false);
        }
        else if (evt.ctrlKey && evt.shiftKey) { // CTRL + SHIFT
            if (evt.keyCode == 65 || evt.keyCode == 86) { // A (65) or V (86)
                bDisableVideo = (evt.keyCode == 65);
                txtCallStatus.innerHTML = '<i>Video ' + (bDisableVideo ? 'disabled' : 'enabled') + '</i>';
                window.localStorage.setItem('org.doubango.expert.disable_video', bDisableVideo);
            }
        }
    }

    function onDivCallCtrlMouseMove(evt) {
        try { // IE: DOM not ready
            if (tsk_utils_have_stream()) {
                btnCall.disabled = (!tsk_utils_have_stream() || !oSipSessionRegister || !oSipSessionRegister.is_connected());
                document.getElementById("divCallCtrl").onmousemove = null; // unsubscribe
            }
        }
        catch (e) { }
    }

    function uiOnConnectionEvent(b_connected, b_connecting) { // should be enum: connecting, connected, terminating, terminated
        btnRegister.disabled = b_connected || b_connecting;
        btnUnRegister.disabled = !b_connected && !b_connecting;
        btnCall.disabled = !(b_connected && tsk_utils_have_webrtc() && tsk_utils_have_stream());
        btnHangUp.disabled = !oSipSessionCall;
    }

    function uiVideoDisplayEvent(b_local, b_added) {
        var o_elt_video = b_local ? videoLocal : videoRemote;

        if (b_added) {
            if (SIPml.isWebRtc4AllSupported()) {
                if (b_local){ if(window.__o_display_local) window.__o_display_local.style.visibility = "visible"; }
                else { if(window.__o_display_remote) window.__o_display_remote.style.visibility = "visible"; }
                   
            }
            else {
                o_elt_video.style.opacity = 1;
            }
            uiVideoDisplayShowHide(true);
        }
        else {
            if (SIPml.isWebRtc4AllSupported()) {
                if (b_local){ if(window.__o_display_local) window.__o_display_local.style.visibility = "hidden"; }
                else { if(window.__o_display_remote) window.__o_display_remote.style.visibility = "hidden"; }
            }
            else{
                o_elt_video.style.opacity = 0;
            }
            fullScreen(false);
        }
    }

    function uiVideoDisplayShowHide(b_show) {
        if (b_show) {
            tdVideo.style.height = '340px';
            divVideo.style.height = navigator.appName == 'Microsoft Internet Explorer' ? '100%' : '340px';
        }
        else {
            tdVideo.style.height = '0px';
            divVideo.style.height = '0px';
        }
        btnFullScreen.disabled = !b_show;
    }

    function uiDisableCallOptions() {
        if(window.localStorage) {
            window.localStorage.setItem('org.doubango.expert.disable_callbtn_options', 'true');
            uiBtnCallSetText('Call');
            alert('Use expert view to enable the options again (/!\\requires re-loading the page)');
        }
    }

    function uiBtnCallSetText(s_text) {
        switch(s_text) {
            case "Call":
                {
                    var bDisableCallBtnOptions = (window.localStorage && window.localStorage.getItem('org.doubango.expert.disable_callbtn_options') == "true");
                    btnCall.value = btnCall.innerHTML = bDisableCallBtnOptions ? 'Call' : 'Call <span id="spanCaret" class="caret">';
                    btnCall.setAttribute("class", bDisableCallBtnOptions ? "btn btn-primary" : "btn btn-primary dropdown-toggle");
                    btnCall.onclick = bDisableCallBtnOptions ? function(){ sipCall(bDisableVideo ? 'call-audio' : 'call-audiovideo'); } : null;
                    ulCallOptions.style.visibility = bDisableCallBtnOptions ? "hidden" : "visible";
                    if(!bDisableCallBtnOptions && ulCallOptions.parentNode != divBtnCallGroup){
                        divBtnCallGroup.appendChild(ulCallOptions);
                    }
                    else if(bDisableCallBtnOptions && ulCallOptions.parentNode == divBtnCallGroup) {
                        document.body.appendChild(ulCallOptions);
                    }
                    
                    break;
                }
            default:
                {
                    btnCall.value = btnCall.innerHTML = s_text;
                    btnCall.setAttribute("class", "btn btn-primary");
                    btnCall.onclick = function(){ sipCall(bDisableVideo ? 'call-audio' : 'call-audiovideo'); };
                    ulCallOptions.style.visibility = "hidden";
                    if(ulCallOptions.parentNode == divBtnCallGroup){
                        document.body.appendChild(ulCallOptions);
                    }
                    break;
                }
        }
    }

    function uiCallTerminated(s_description){
        uiBtnCallSetText("Call");
        btnHangUp.value = 'HangUp';
        btnHoldResume.value = 'hold';
        btnCall.disabled = false;
        btnHangUp.disabled = true;

        oSipSessionCall = null;

        stopRingbackTone();
        stopRingTone();

        txtCallStatus.innerHTML = "<i>" + s_description + "</i>";
        uiVideoDisplayShowHide(false);
        divCallOptions.style.opacity = 0;

        if (oNotifICall) {
            oNotifICall.cancel();
            oNotifICall = null;
        }

        uiVideoDisplayEvent(true, false);
        uiVideoDisplayEvent(false, false);

        setTimeout(function () { if (!oSipSessionCall) txtCallStatus.innerHTML = ''; }, 2500);
    }

    // Callback function for SIP Stacks
    function onSipEventStack(e /*SIPml.Stack.Event*/) {
        tsk_utils_log_info('==stack event = ' + e.type);
        switch (e.type) {
            case 'started':
                {
                    // catch exception for IE (DOM not ready)
                    try {
                        // LogIn (REGISTER) as soon as the stack finish starting
                        oSipSessionRegister = this.newSession('register', {
                            expires: 200,
                            events_listener: { events: '*', listener: onSipEventSession },
                            sip_caps: [
                                        { name: '+g.oma.sip-im', value: null },
                                        { name: '+audio', value: null },
                                        { name: 'language', value: '\"en,fr\"' }
                                ]
                        });
                        oSipSessionRegister.register();
                    }
                    catch (e) {
                        txtRegStatus.value = txtRegStatus.innerHTML = "<b>1:" + e + "</b>";
                        btnRegister.disabled = false;
                    }
                    break;
                }
            case 'stopping': case 'stopped': case 'failed_to_start': case 'failed_to_stop':
                {
                    var bFailure = (e.type == 'failed_to_start') || (e.type == 'failed_to_stop');
                    oSipStack = null;
                    oSipSessionRegister = null;
                    oSipSessionCall = null;

                    uiOnConnectionEvent(false, false);

                    stopRingbackTone();
                    stopRingTone();

                    uiVideoDisplayShowHide(false);
                    divCallOptions.style.opacity = 0;

                    txtCallStatus.innerHTML = '';
                    txtRegStatus.innerHTML = bFailure ? "<i>Disconnected: <b>" + e.description + "</b></i>" : "<i>Disconnected</i>";
                    break;
                }

            case 'i_new_call':
                {
                    if (oSipSessionCall) {
                        // do not accept the incoming call if we're already 'in call'
                        e.newSession.hangup(); // comment this line for multi-line support
                    }
                    else {
                        oSipSessionCall = e.newSession;
                        // start listening for events
                        oSipSessionCall.setConfiguration(oConfigCall);

                        uiBtnCallSetText('Answer');
                        btnHangUp.value = 'Reject';
                        btnCall.disabled = false;
                        btnHangUp.disabled = false;

                        startRingTone();

                        var sRemoteNumber = (oSipSessionCall.getRemoteFriendlyName() || 'unknown');
                        txtCallStatus.innerHTML = "<i>Incoming call from [<b>" + sRemoteNumber + "</b>]</i>";
                        showNotifICall(sRemoteNumber);
                    }
                    break;
                }

            case 'm_permission_requested':
                {
                    divGlassPanel.style.visibility = 'visible';
                    break;
                }
            case 'm_permission_accepted':
            case 'm_permission_refused':
                {
                    divGlassPanel.style.visibility = 'hidden';
                    if(e.type == 'm_permission_refused'){
                        uiCallTerminated('Media stream permission denied');
                    }
                    break;
                }

            case 'starting': default: break;
        }
    };

    // Callback function for SIP sessions (INVITE, REGISTER, MESSAGE...)
    function onSipEventSession(e /* SIPml.Session.Event */) {
        tsk_utils_log_info('==session event = ' + e.type);

        switch (e.type) {
            case 'connecting': case 'connected':
                {
                    var bConnected = (e.type == 'connected');
                    if (e.session == oSipSessionRegister) {
                        uiOnConnectionEvent(bConnected, !bConnected);
                        txtRegStatus.innerHTML = "<i>" + e.description + "</i>";
                    }
                    else if (e.session == oSipSessionCall) {
                        btnHangUp.value = 'HangUp';
                        btnCall.disabled = true;
                        btnHangUp.disabled = false;
                        btnTransfer.disabled = false;

                        if (bConnected) {
                            stopRingbackTone();
                            stopRingTone();

                            if (oNotifICall) {
                                oNotifICall.cancel();
                                oNotifICall = null;
                            }
                        }

                        txtCallStatus.innerHTML = "<i>" + e.description + "</i>";
                        divCallOptions.style.opacity = bConnected ? 1 : 0;

                        if (SIPml.isWebRtc4AllSupported()) { // IE don't provide stream callback
                            uiVideoDisplayEvent(true, true);
                            uiVideoDisplayEvent(false, true);
                        }
                    }
                    break;
                } // 'connecting' | 'connected'
            case 'terminating': case 'terminated':
                {
                    if (e.session == oSipSessionRegister) {
                        uiOnConnectionEvent(false, false);

                        oSipSessionCall = null;
                        oSipSessionRegister = null;

                        txtRegStatus.innerHTML = "<i>" + e.description + "</i>";
                    }
                    else if (e.session == oSipSessionCall) {
                        uiCallTerminated(e.description);
                    }
                    break;
                } // 'terminating' | 'terminated'

            case 'm_stream_video_local_added':
                {
                    if (e.session == oSipSessionCall) {
                        uiVideoDisplayEvent(true, true);
                    }
                    break;
                }
            case 'm_stream_video_local_removed':
                {
                    if (e.session == oSipSessionCall) {
                        uiVideoDisplayEvent(true, false);
                    }
                    break;
                }
            case 'm_stream_video_remote_added':
                {
                    if (e.session == oSipSessionCall) {
                        uiVideoDisplayEvent(false, true);
                    }
                    break;
                }
            case 'm_stream_video_remote_removed':
                {
                    if (e.session == oSipSessionCall) {
                        uiVideoDisplayEvent(false, false);
                    }
                    break;
                }

            case 'm_stream_audio_local_added':
            case 'm_stream_audio_local_removed':
            case 'm_stream_audio_remote_added':
            case 'm_stream_audio_remote_removed':
                {
                    break;
                }

            case 'i_ect_new_call':
                {
                    oSipSessionTransferCall = e.session;
                    break;
                }

            case 'i_ao_request':
                {
                    if(e.session == oSipSessionCall){
                        var iSipResponseCode = e.getSipResponseCode();
                        if (iSipResponseCode == 180 || iSipResponseCode == 183) {
                            startRingbackTone();
                            txtCallStatus.innerHTML = '<i>Remote ringing...</i>';
                        }
                    }
                    break;
                }

            case 'm_early_media':
                {
                    if(e.session == oSipSessionCall){
                        stopRingbackTone();
                        stopRingTone();
                        txtCallStatus.innerHTML = '<i>Early media started</i>';
                    }
                    break;
                }

            case 'm_local_hold_ok':
                {
                    if(e.session == oSipSessionCall){
                        if (oSipSessionCall.bTransfering) {
                            oSipSessionCall.bTransfering = false;
                            // this.AVSession.TransferCall(this.transferUri);
                        }
                        btnHoldResume.value = 'Resume';
                        btnHoldResume.disabled = false;
                        txtCallStatus.innerHTML = '<i>Call placed on hold</i>';
                        oSipSessionCall.bHeld = true;
                    }
                    break;
                }
            case 'm_local_hold_nok':
                {
                    if(e.session == oSipSessionCall){
                        oSipSessionCall.bTransfering = false;
                        btnHoldResume.value = 'Hold';
                        btnHoldResume.disabled = false;
                        txtCallStatus.innerHTML = '<i>Failed to place remote party on hold</i>';
                    }
                    break;
                }
            case 'm_local_resume_ok':
                {
                    if(e.session == oSipSessionCall){
                        oSipSessionCall.bTransfering = false;
                        btnHoldResume.value = 'Hold';
                        btnHoldResume.disabled = false;
                        txtCallStatus.innerHTML = '<i>Call taken off hold</i>';
                        oSipSessionCall.bHeld = false;

                        if (SIPml.isWebRtc4AllSupported()) { // IE don't provide stream callback yet
                            uiVideoDisplayEvent(true, true);
                            uiVideoDisplayEvent(false, true);
                        }
                    }
                    break;
                }
            case 'm_local_resume_nok':
                {
                    if(e.session == oSipSessionCall){
                        oSipSessionCall.bTransfering = false;
                        btnHoldResume.disabled = false;
                        txtCallStatus.innerHTML = '<i>Failed to unhold call</i>';
                    }
                    break;
                }
            case 'm_remote_hold':
                {
                    if(e.session == oSipSessionCall){
                        txtCallStatus.innerHTML = '<i>Placed on hold by remote party</i>';
                    }
                    break;
                }
            case 'm_remote_resume':
                {
                    if(e.session == oSipSessionCall){
                        txtCallStatus.innerHTML = '<i>Taken off hold by remote party</i>';
                    }
                    break;
                }

            case 'o_ect_trying':
                {
                    if(e.session == oSipSessionCall){
                        txtCallStatus.innerHTML = '<i>Call transfer in progress...</i>';
                    }
                    break;
                }
            case 'o_ect_accepted':
                {
                    if(e.session == oSipSessionCall){
                        txtCallStatus.innerHTML = '<i>Call transfer accepted</i>';
                    }
                    break;
                }
            case 'o_ect_completed':
            case 'i_ect_completed':
                {
                    if(e.session == oSipSessionCall){
                        txtCallStatus.innerHTML = '<i>Call transfer completed</i>';
                        btnTransfer.disabled = false;
                        if (oSipSessionTransferCall) {
                            oSipSessionCall = oSipSessionTransferCall;
                        }
                        oSipSessionTransferCall = null;
                    }
                    break;
                }
            case 'o_ect_failed':
            case 'i_ect_failed':
                {
                    if(e.session == oSipSessionCall){
                        txtCallStatus.innerHTML = '<i>Call transfer failed</i>';
                        btnTransfer.disabled = false;
                    }
                    break;
                }
            case 'o_ect_notify':
            case 'i_ect_notify':
                {
                    if(e.session == oSipSessionCall){
                        txtCallStatus.innerHTML = "<i>Call Transfer: <b>" + e.getSipResponseCode() + " " + e.description + "</b></i>";
                        if (e.getSipResponseCode() >= 300) {
                            if (oSipSessionCall.bHeld) {
                                oSipSessionCall.resume();
                            }
                            btnTransfer.disabled = false;
                        }
                    }
                    break;
                }
            case 'i_ect_requested':
                {
                    if(e.session == oSipSessionCall){                        
                        var s_message = "Do you accept call transfer to [" + e.getTransferDestinationFriendlyName() + "]?";//FIXME
                        if (confirm(s_message)) {
                            txtCallStatus.innerHTML = "<i>Call transfer in progress...</i>";
                            oSipSessionCall.acceptTransfer();
                            break;
                        }
                        oSipSessionCall.rejectTransfer();
                    }
                    break;
                }
        }
    }
