# Copyright (C) 2009 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

# the purpose of this sample is to demonstrate how one can
# generate two distinct shared libraries and have them both
# uploaded in
#


LOCAL_PATH:= $(call my-dir)

# encode
#
include $(CLEAR_VARS)
LOCAL_EXPORT_LDLIBS :=-llog

LOCAL_MODULE    := libscalecomm-encode
LOCAL_SRC_FILES := en_code.c

include $(BUILD_SHARED_LIBRARY)

# cmd_data
#
include $(CLEAR_VARS)

LOCAL_MODULE    := libscalecomm-cmddata
LOCAL_SRC_FILES := cmd_data.c

LOCAL_STATIC_LIBRARIES := libscalecomm-encode

include $(BUILD_SHARED_LIBRARY)

# subcmd_data
#
include $(CLEAR_VARS)

LOCAL_MODULE    := libscalecomm-subcmddata
LOCAL_SRC_FILES := subcmd_data.c

LOCAL_STATIC_LIBRARIES := libscalecomm-cmddata libscalecomm-encode

include $(BUILD_SHARED_LIBRARY)


# wrapper lib,
#
include $(CLEAR_VARS)

LOCAL_MODULE    := libscalecomm-wrapper
LOCAL_SRC_FILES := wrapper.c

LOCAL_STATIC_LIBRARIES := libtwolib-second libscalecomm-cmddata libscalecomm-encode libscalecomm-subcmddata

# isp lib
include $(CLEAR_VARS)
LOCAL_LDLIBS := -llog
LOCAL_MODULE    := libscalecomm-isp
LOCAL_SRC_FILES := isp.c

include $(BUILD_SHARED_LIBRARY)


