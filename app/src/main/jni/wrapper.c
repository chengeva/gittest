#include <jni.h>
#include "en_code.h"
#include "cmd_data.h"
#include <android/log.h>
#include "subcmd_data.h"

float data_to_float(unsigned int n_data, unsigned char n_dp) {
	float f_rv = n_data;
	signed char n_dp_buf = (signed char) n_dp;
	int n_loop = 0;
	if (n_dp_buf >= 0) {
		for (n_loop = 0; n_loop < n_dp_buf; n_loop++)
			f_rv /= 10;
	} else {
		n_dp_buf = -n_dp_buf;
		for (n_loop = 0; n_loop < n_dp_buf; n_loop++)
			f_rv *= 10;
	}
	return f_rv;
}

unsigned int fourBytesToUInt(unsigned char byte1, unsigned char byte2,
		unsigned char byte3, unsigned char byte4) {
	unsigned int int1 = (unsigned int) byte1;
	unsigned int int2 = (unsigned int) byte2;
	unsigned int int3 = (unsigned int) byte3;
	unsigned int int4 = (unsigned int) byte4;
	unsigned int intReturn;
	intReturn = int4 << 24 | int3 << 16 | int2 << 8 | int1;
	return intReturn;
}

jfloat Java_com_acaia_scale_communications_AcaiaCommunicationPacketHelper_getweightunit(
		JNIEnv* env, jobject this, jbyteArray cStringData,
		jint cStringDataLength) {
	
	cmd_setting *lo_setting;
	unsigned char* s_data;
	char ls_decode[256];
	char *ls_buf;

	char buffer[256];
	(*env)->GetByteArrayRegion(env, cStringData, 0, cStringDataLength, buffer);
	ls_buf = buffer;
	unsigned char ln_pass = 0, ln_cmd = 0, ln_id = 0;
	int ln_datalen = cStringDataLength;
	unsigned char ln_len;
	jcharArray result = (*env)->NewByteArray(env, 256);
	float res;

	while (ln_datalen > 0) {
		switch (sr_decrypt(ls_buf, cStringDataLength, ls_decode, &ln_cmd,
				&ln_id, &ln_pass)) {
		case e_encry_tobe_continue:
			return;
		case e_encry_success:
			switch (ln_cmd) {
			case e_cmd_weight_r:
				(wt_rep*) ls_decode;
				float inputWeight = data_to_float(((wt_rep*) ls_decode)->n_data,
						((wt_rep*) ls_decode)->n_dp);
				//(*env)->SetByteArrayRegion(env, result, 0, 256, ls_decode);
				switch (((wt_rep*) ls_decode)->n_unit) {
				default:
				case e_unit_g:
					return 0;
					break;
				case e_unit_lb:

					break;
				case e_unit_oz:

					return 1;
					break;
				}
				break;
			}

			break;

		default:
			break;
		}
		ls_buf += ln_pass;
		ln_datalen -= ln_pass;
	}

}

jfloat Java_com_acaia_scale_communications_AcaiaCommunicationPacketHelper_parsescalepacket(
		JNIEnv* env, jobject this, jbyteArray cStringData,
		jint cStringDataLength) {
	cmd_setting *lo_setting;
	unsigned char* s_data;
	char ls_decode[256];
	char *ls_buf;

	char buffer[256];
	(*env)->GetByteArrayRegion(env, cStringData, 0, cStringDataLength, buffer);
	ls_buf = buffer;
	unsigned char ln_pass = 0, ln_cmd = 0, ln_id = 0;
	int ln_datalen = cStringDataLength;
	unsigned char ln_len;
	jcharArray result = (*env)->NewByteArray(env, 256);
	float res;

	while (ln_datalen > 0) {
		switch (sr_decrypt(ls_buf, cStringDataLength, ls_decode, &ln_cmd,
				&ln_id, &ln_pass)) {
		case e_encry_tobe_continue:
			return;
		case e_encry_success:
			switch (ln_cmd) {
			case e_cmd_weight_r:
				(wt_rep*) ls_decode;
				jboolean illegalDataDetected = 0;

				float inputWeight = data_to_float(((wt_rep*) ls_decode)->n_data,
						((wt_rep*) ls_decode)->n_dp);
				if (((wt_rep*) ls_decode)->b_positive == e_wt_negative)
					inputWeight *= -1;
				;


				// Invalid weight filter:
				// type >= e_weight_size = error
				if (((wt_rep*) ls_decode)->n_type >= e_weight_size)
					illegalDataDetected = 1;
				if (((wt_rep*) ls_decode)->n_type == 0)
					illegalDataDetected = 1;
				// unit >= e_unit_size  = error
				if (((wt_rep*) ls_decode)->n_unit >= e_unit_size)
					illegalDataDetected = 1;
				// dp >= 6  = error
				if (((wt_rep*) ls_decode)->n_dp >= 6)
					illegalDataDetected = 1;
				if (inputWeight > 3000 || inputWeight < -3000)
					illegalDataDetected = 1;
				if(illegalDataDetected!=1){

					return inputWeight;
				}else{
					return 9999;
				}
				break;

			case e_cmd_battery_r:
				__android_log_print(ANDROID_LOG_INFO, "JNIMsg", "[[e_cmd_battery_r]]");
				return ((bat_rep*) ls_decode)->n_battery;
				break;

			case e_cmd_custom:

				s_data = (unsigned char*) &ls_decode[1];
				lo_setting = (cmd_setting*) s_data;
				switch ((unsigned char) ls_decode[0]) {

				case e_subcmd_key: // [app -> scale] general a key event.
				case e_subcmd_unit: // [app -> scale] change unit.
				case e_subcmd_set_setting: // [app -> scale] set setting.
				case e_subcmd_read_setting: // [app -> scale] ask for setting data.
					// unused for APP
					break;
				case e_subcmd_setting_response: // [scale -> app] response of e_subcmd_read_setting.
					switch (lo_setting->n_set_id) {
					case e_setting_sleep: // Auto switch off setting
						// NSLog(@"sleep setting value=%d", lo_setting->n_set_value);
						return lo_setting->n_set_value;
						break;
					case e_setting_keydisable: // Touch key disable remaining second
						return lo_setting->n_set_value;

						// NSLog(@"2.key disable value=%d", lo_setting->n_set_value);
						break;
					case e_setting_sound:
						// Got current touch key sound setting.
						return lo_setting->n_set_value;
						break;
		            case e_setting_capability: // korean version 20140827
		                // NSLog(@"Receive e_setting_capability: %d",(int) lo_setting->n_set_value);
		                return lo_setting->n_set_value;
		                break;
					}
					break;

				}

				switch ((unsigned char) ls_decode[0]) {
				case e_subcmd_timer_response: // timer running

					res = fourBytesToUInt((unsigned char) ls_decode[1],
							(unsigned char) ls_decode[2],
							(unsigned char) ls_decode[3],
							(unsigned char) ls_decode[4] / 10.0f);

					return res;
					break;
				case e_subcmd_ptimer_response: // paused timer
					res = fourBytesToUInt((unsigned char) ls_decode[1],
							(unsigned char) ls_decode[2],
							(unsigned char) ls_decode[3],
							(unsigned char) ls_decode[4] / 10.0f);
					//return 1111;
					return res;
					break;
					// return weighing AD value
				case e_subcmd_ad_response:
					// NSLog(@"got ad= %u", *((u4*)&ls_decode[1]));
					break;
					// return Count Down value
				case e_subcmd_cd_response:
				case e_subcmd_pcd_response:
					break;
				}
			}

			break;

		default:
			break;
		}
		ls_buf += ln_pass;
		ln_datalen -= ln_pass;
	}

}

jbyteArray Java_com_acaia_scale_communications_AcaiaCommunicationPacketHelper_askautooff(
		JNIEnv* env, jobject this) {
	unsigned char ln_len;
	char ls_buf[32];
	jcharArray result;
	cmd_setting lo_setting;
	lo_setting.n_set_id = e_setting_sleep;
	lo_setting.n_set_value = 1;
	ln_len = pack_custom(ls_buf, e_subcmd_read_setting,
			(unsigned char*) &lo_setting, 2);
	result = (*env)->NewByteArray(env, ln_len);
	(*env)->SetByteArrayRegion(env, result, 0, ln_len, ls_buf);
	return result;
}

jbyteArray Java_com_acaia_scale_communications_AcaiaCommunicationPacketHelper_askbeepsound(
		JNIEnv* env, jobject this) {
	unsigned char ln_len;
	char ls_buf[32];
	jcharArray result;
	cmd_setting lo_setting;
	lo_setting.n_set_id = e_setting_sound;
	lo_setting.n_set_value = 2; // unused
	ln_len = pack_custom(ls_buf, e_subcmd_read_setting,
			(unsigned char*) &lo_setting, 2);
	result = (*env)->NewByteArray(env, ln_len);

	(*env)->SetByteArrayRegion(env, result, 0, ln_len, ls_buf);
	return result;
}

jbyteArray Java_com_acaia_scale_communications_AcaiaCommunicationPacketHelper_askdisablekeyseconds(
		JNIEnv* env, jobject this, jint second) {
	unsigned char ln_len;
	char ls_buf[32];
	jcharArray result;
	cmd_setting lo_setting;
	lo_setting.n_set_id = e_setting_keydisable;
	lo_setting.n_set_value = 1;
	ln_len = pack_custom(ls_buf, e_subcmd_read_setting,
			(unsigned char*) &lo_setting, 2);
	result = (*env)->NewByteArray(env, ln_len);
	(*env)->SetByteArrayRegion(env, result, 0, ln_len, ls_buf);
	return result;
}

jbyteArray Java_com_acaia_scale_communications_AcaiaCommunicationPacketHelper_getscaletimer(
		JNIEnv* env, jobject this, jint second) {
	unsigned char ln_len;
	char ls_buf[32];
	jcharArray result;
	ln_len = pack_get_timer(ls_buf, 20);
	result = (*env)->NewByteArray(env, ln_len);
	(*env)->SetByteArrayRegion(env, result, 0, ln_len, ls_buf);
	return result;
}

jbyteArray Java_com_acaia_scale_communications_AcaiaCommunicationPacketHelper_stopscaletimer(
		JNIEnv* env, jobject this, jint second) {
	unsigned char ln_len;
	char ls_buf[32];
	jcharArray result;
	ln_len = pack_stop_timer(ls_buf);
	result = (*env)->NewByteArray(env, ln_len);
	(*env)->SetByteArrayRegion(env, result, 0, ln_len, ls_buf);
	return result;
}

jbyteArray Java_com_acaia_scale_communications_AcaiaCommunicationPacketHelper_pausescaletimer(
		JNIEnv* env, jobject this, jint second) {
	unsigned char ln_len;
	char ls_buf[32];
	jcharArray result;
	ln_len = pack_pause_timer(ls_buf);
	result = (*env)->NewByteArray(env, ln_len);
	(*env)->SetByteArrayRegion(env, result, 0, ln_len, ls_buf);
	return result;
}

jbyteArray Java_com_acaia_scale_communications_AcaiaCommunicationPacketHelper_startscaletimer(
		JNIEnv* env, jobject this, jint second) {
	unsigned char ln_len;
	char ls_buf[32];
	jcharArray result;
	ln_len = pack_start_timer(ls_buf);
	result = (*env)->NewByteArray(env, ln_len);
	(*env)->SetByteArrayRegion(env, result, 0, ln_len, ls_buf);
	return result;
}

jbyteArray Java_com_acaia_scale_communications_AcaiaCommunicationPacketHelper_getaskdisablekeycmd(
		JNIEnv* env, jobject this) {
	unsigned char ln_len;
	char ls_buf[32];
	jcharArray result;

	cmd_setting lo_setting;
	lo_setting.n_set_id = e_setting_keydisable;
	lo_setting.n_set_value = 1;
	ln_len = pack_custom(ls_buf, e_subcmd_read_setting,
			(unsigned char*) &lo_setting, 2);
	result = (*env)->NewByteArray(env, ln_len);

	(*env)->SetByteArrayRegion(env, result, 0, ln_len, ls_buf);

	return result;
}

jbyteArray Java_com_acaia_scale_communications_AcaiaCommunicationPacketHelper_getdisablekeycmd(
		JNIEnv* env, jobject this, jint second) {
	unsigned char ln_len;
	char ls_buf[32];
	jcharArray result;

	cmd_setting lo_setting;
	lo_setting.n_set_id = e_setting_keydisable;
	lo_setting.n_set_value = second;
	ln_len = pack_custom(ls_buf, e_subcmd_set_setting,
			(unsigned char*) &lo_setting, 2);
	result = (*env)->NewByteArray(env, ln_len);

	(*env)->SetByteArrayRegion(env, result, 0, ln_len, ls_buf);

	return result;
}

jbyteArray Java_com_acaia_scale_communications_AcaiaCommunicationPacketHelper_getswitchunitgramcmd(
		JNIEnv* env, jobject this, jint n_period, jint n_time) {
	unsigned char ln_len;
	char ls_buf[32];
	jcharArray result;
	unsigned char ln_id = e_key_tare;
	ln_id = e_unit_g;
	ln_len = pack_custom(ls_buf, e_subcmd_unit, &ln_id, 1);
	result = (*env)->NewByteArray(env, ln_len);

	(*env)->SetByteArrayRegion(env, result, 0, ln_len, ls_buf);
	return result;
}

jbyteArray Java_com_acaia_scale_communications_AcaiaCommunicationPacketHelper_getswitchunitozcmd(
		JNIEnv* env, jobject this, jint n_period, jint n_time) {
	unsigned char ln_len;
	char ls_buf[32];
	jcharArray result;
	unsigned char ln_id = e_key_tare;
	ln_id = e_unit_oz;
	ln_len = pack_custom(ls_buf, e_subcmd_unit, &ln_id, 1);
	result = (*env)->NewByteArray(env, ln_len);
	(*env)->SetByteArrayRegion(env, result, 0, ln_len, ls_buf);
	return result;
}

jbyteArray Java_com_acaia_scale_communications_AcaiaCommunicationPacketHelper_getweightcmd(
		JNIEnv* env, jobject this, jint n_period, jint n_time) {
	unsigned char ln_len;

	char ls_buf[32];

	jcharArray result;

	ln_len = pack_weight_req(ls_buf, n_period, n_time, e_weight_net);
	result = (*env)->NewByteArray(env, ln_len);

	(*env)->SetByteArrayRegion(env, result, 0, ln_len, ls_buf);

	return result;
}

jbyteArray Java_com_acaia_scale_communications_AcaiaCommunicationPacketHelper_getautooffcmd(
		JNIEnv* env, jobject this, jint time) {
	unsigned char ln_len;
	unsigned char ln_id;
	char ls_buf[32];
	cmd_setting lo_setting;
	lo_setting.n_set_id = e_setting_sleep;
	lo_setting.n_set_value = time;
	ln_len = pack_custom(ls_buf, e_subcmd_set_setting,
			(unsigned char*) &lo_setting, 2);
	jcharArray result;
	result = (*env)->NewByteArray(env, ln_len);

	(*env)->SetByteArrayRegion(env, result, 0, ln_len, ls_buf);

	return result;
}

jbyteArray Java_com_acaia_scale_communications_AcaiaCommunicationPacketHelper_getbeepcmd(
		JNIEnv* env, jobject this, jboolean beepON) {
	unsigned char ln_len;
	unsigned char ln_id;
	char ls_buf[32];
	if (beepON)
		ln_len = pack_audio_req(ls_buf, 1);
	else
		ln_len = pack_audio_req(ls_buf, 0);
	jcharArray result;
	result = (*env)->NewByteArray(env, ln_len);

	(*env)->SetByteArrayRegion(env, result, 0, ln_len, ls_buf);

	return result;
}

jbyteArray Java_com_acaia_scale_communications_AcaiaCommunicationPacketHelper_getbatterycmd(
		JNIEnv* env, jobject this) {
	unsigned char ln_len;
	unsigned char ln_id;
	char ls_buf[32];

__android_log_print(ANDROID_LOG_INFO, "JNIMsg", "[[getbatterycmd]]");
				
	jcharArray result;

	ln_len = pack_battery_req(ls_buf);
	result = (*env)->NewByteArray(env, ln_len);

	(*env)->SetByteArrayRegion(env, result, 0, ln_len, ls_buf);

	return result;
}

jbyteArray Java_com_acaia_scale_communications_AcaiaCommunicationPacketHelper_gettarecmd(
		JNIEnv* env, jobject this) {
	unsigned char ln_len;
	unsigned char ln_id;
	char ls_buf[32];

	jcharArray result;

	ln_id = e_key_tare;
	ln_len = pack_custom(ls_buf, e_subcmd_key, &ln_id, 1);
	result = (*env)->NewByteArray(env, ln_len);

	(*env)->SetByteArrayRegion(env, result, 0, ln_len, ls_buf);

	return result;
}

jint Java_com_acaia_scale_communications_AcaiaCommunicationPacketHelper_getsubdatavalue(
		JNIEnv* env, jobject this, jbyteArray cStringData,
		jint cStringDataLength) {
	cmd_setting *lo_setting;
	unsigned char* s_data;
	char ls_decode[256];
	char *ls_buf;

	char buffer[256];
	(*env)->GetByteArrayRegion(env, cStringData, 0, cStringDataLength, buffer);
	ls_buf = buffer;
	unsigned char ln_pass = 0, ln_cmd = 0, ln_id = 0;
	int ln_datalen = cStringDataLength;
	unsigned char ln_len;
	jcharArray result = (*env)->NewByteArray(env, 256);

	while (ln_datalen > 0) {
		switch (sr_decrypt(ls_buf, cStringDataLength, ls_decode, &ln_cmd,
				&ln_id, &ln_pass)) {
		case e_encry_tobe_continue:
			return;
		case e_encry_success:
			switch (ln_cmd) {

			case e_cmd_custom:

				s_data = (unsigned char*) &ls_decode[1];
				lo_setting = (cmd_setting*) s_data;
				switch ((unsigned char) ls_decode[0]) {

				case e_subcmd_key: // [app -> scale] general a key event.
				case e_subcmd_unit: // [app -> scale] change unit.
				case e_subcmd_set_setting: // [app -> scale] set setting.
				case e_subcmd_read_setting: // [app -> scale] ask for setting data.
					// unused for APP
					break;
				case e_subcmd_setting_response: // [scale -> app] response of e_subcmd_read_setting.
					return lo_setting->n_set_value;

					break;
				}
				return (unsigned char) ls_decode[0];

			}

			break;

		default:
			break;
		}
		ls_buf += ln_pass;
		ln_datalen -= ln_pass;
	}

}

jint Java_com_acaia_scale_communications_AcaiaCommunicationPacketHelper_getsubdatatype(
		JNIEnv* env, jobject this, jbyteArray cStringData,
		jint cStringDataLength) {
	cmd_setting *lo_setting;
	unsigned char* s_data;
	char ls_decode[256];
	char *ls_buf;

	char buffer[256];
	(*env)->GetByteArrayRegion(env, cStringData, 0, cStringDataLength, buffer);
	ls_buf = buffer;
	unsigned char ln_pass = 0, ln_cmd = 0, ln_id = 0;
	int ln_datalen = cStringDataLength;
	unsigned char ln_len;
	jcharArray result = (*env)->NewByteArray(env, 256);

	while (ln_datalen > 0) {
		switch (sr_decrypt(ls_buf, cStringDataLength, ls_decode, &ln_cmd,
				&ln_id, &ln_pass)) {
		case e_encry_tobe_continue:
			return;
		case e_encry_success:
			switch (ln_cmd) {

			case e_cmd_custom:

				s_data = (unsigned char*) &ls_decode[1];
				lo_setting = (cmd_setting*) s_data;
				switch ((unsigned char) ls_decode[0]) {

				case e_subcmd_key: // [app -> scale] general a key event.
				case e_subcmd_unit: // [app -> scale] change unit.
				case e_subcmd_set_setting: // [app -> scale] set setting.
				case e_subcmd_read_setting: // [app -> scale] ask for setting data.
					// unused for APP
					break;
				case e_subcmd_setting_response: // [scale -> app] response of e_subcmd_read_setting.
					return lo_setting->n_set_id;

					break;
				}
				return (unsigned char) ls_decode[0];

			}

			break;

		default:
			break;
		}
		ls_buf += ln_pass;
		ln_datalen -= ln_pass;
	}

}

jint Java_com_acaia_scale_communications_AcaiaCommunicationPacketHelper_getdatatype(
		JNIEnv* env, jobject this, jbyteArray cStringData,
		jint cStringDataLength) {

	char ls_decode[256];
	char *ls_buf;

	char buffer[256];
	(*env)->GetByteArrayRegion(env, cStringData, 0, cStringDataLength, buffer);
	ls_buf = buffer;
	unsigned char ln_pass = 0, ln_cmd = 0, ln_id = 0;
	int ln_datalen = cStringDataLength;
	unsigned char ln_len;
	jcharArray result = (*env)->NewByteArray(env, 256);

	while (ln_datalen > 0) {
		switch (sr_decrypt(ls_buf, cStringDataLength, ls_decode, &ln_cmd,
				&ln_id, &ln_pass)) {
		case e_encry_tobe_continue:
			return;
		case e_encry_success:

			return ln_cmd;
			break;

		default:
			break;
		}
		ls_buf += ln_pass;
		ln_datalen -= ln_pass;
	}

}

// korean version 20140827
// ask for capacity
// Only the scales with firmware 1.7+ will response
jbyteArray Java_com_acaia_scale_communications_AcaiaCommunicationPacketHelper_askcapacity(
		JNIEnv* env, jobject this) {
	unsigned char ln_len;
	char ls_buf[32];
	jcharArray result;
	cmd_setting lo_setting;
	lo_setting.n_set_id = e_setting_capability;
	lo_setting.n_set_value = 0;
	ln_len = pack_custom(ls_buf, e_subcmd_read_setting,
			(unsigned char*) &lo_setting, 2);
	result = (*env)->NewByteArray(env, ln_len);
	(*env)->SetByteArrayRegion(env, result, 0, ln_len, ls_buf);
	return result;
}

// korean version 20140827
// set capacity
jbyteArray Java_com_acaia_scale_communications_AcaiaCommunicationPacketHelper_setcapacity(
		JNIEnv* env, jobject this, jint val) {
	unsigned char ln_len;
	unsigned char ln_id;
	char ls_buf[32];
	cmd_setting lo_setting;
	lo_setting.n_set_id = e_setting_capability;
	lo_setting.n_set_value = val;
	ln_len = pack_custom(ls_buf, e_subcmd_set_setting,
			(unsigned char*) &lo_setting, 2);
	jcharArray result;
	result = (*env)->NewByteArray(env, ln_len);

	(*env)->SetByteArrayRegion(env, result, 0, ln_len, ls_buf);

	return result;
}
