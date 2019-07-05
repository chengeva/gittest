#ifndef CMD_DATA_H
#define CMD_DATA_H
enum ECMD
{
	e_cmd_none = 0,
	e_cmd_str = 1,
	e_cmd_battery,
	e_cmd_battery_r,
	e_cmd_weight,
	e_cmd_weight_r,
	e_cmd_weight_r2,
	e_cmd_tare,
	e_cmd_sound,
	e_cmd_sound_on,
	e_cmd_light_on,
	e_cmd_file,
	e_cmd_custom,
    // 20140725 -s
    e_cmd_info_get,
    e_cmd_info_sent,
    e_cmd_isp,
    // 20140725 -e
	e_cmd_size
};

char check_cmdlen(unsigned char n_cmd, unsigned char n_len);


//·s¼W
// 20140725 -s
// e_cmd_info
typedef struct SSCALE_INFO
{
    char n_info_version;
    char n_ISP_version;
    char n_firm_mversion;
    char n_firm_sversion;
} scale_info;
#define INFO_LEN    4
// 20140725 -e

// 20140725 -s
unsigned char pack_isp_req(char *s_out);
unsigned char pack_info_req(char *s_out);
// 20140725 -e


// e_cmd_battery

// e_cmd_battery_r
typedef struct SBATTERY_RESPONSE
{
	unsigned char n_battery;
}bat_rep;
#define BATTERY_R_LEN	1

// e_cmd_weight
enum EWEIGHT_TYPE
{
	e_weight_none			= 0x00,
	e_weight_net			= 0x01,
	e_weight_gross			= 0x02,
	e_weight_tare			= 0x03,
	e_weight_goss_tare		= 0x04,
    e_weight_size
};

typedef struct SWEIGHT_REQUEST
{
	unsigned char n_period;			// send weight once every 0.1 * XX seconds.
	unsigned char n_time;			// send weight how many times.
	unsigned char n_type;			// 0 : gross & net 1 : gross 2 : net 3 : tare
}wt_req;
#define WEIGHT_LEN	3

// e_cmd_weight_r
// unit
#ifndef EUNIT_
enum EUNIT
{
	e_unit_off,
	e_unit_kg,
	e_unit_g,
	e_unit_lb,
	e_unit_ct,
	e_unit_oz,
	e_unit_t,
	e_unit_ozt,
	e_unit_gin,
	e_unit_pcs,
	e_unit_pct,
	e_unit_size
};
#endif

enum EWEIGHT_STBLE
{
	e_wt_stable = 0,
	e_wt_unstable = 1
};

enum EWEIGHT_POSITIVE
{
	e_wt_positive = 0,
	e_wt_negative = 1
};

typedef struct SWEIGHT_RESPONSE
{
    unsigned int	n_data;
    unsigned char	n_dp;
    unsigned char	n_unit;			//
    unsigned char	b_stable:1;		// 0 : stable 1 : unstable
    unsigned char	b_positive:1;	// 0 : positive 1 : negative
    unsigned char	n_type:6;
}wt_rep;
#define WEIGHT_R_LEN	7

typedef struct SWEIGHT_RESPONSE2
{
    unsigned int	n_data1;
    unsigned int	n_data2;
    unsigned char	n_dp1;
    unsigned char	n_dp2;
    unsigned char	n_unit;			//
    unsigned char	b_stable:1;		// 0 : stable 1 : unstable
    unsigned char	b_positive:1;	// 0 : positive 1 : negative
    unsigned char	n_type:6;
}wt_rep2;
#define WEIGHT_R2_LEN	12

// e_cmd_tare
typedef struct STARE_REQUEST
{
	unsigned int	n_data;
	unsigned char	n_dp;
	unsigned char	n_unit;		//
}tare_req;
#define TARE_LEN	6

// e_cmd_sound
typedef struct SSOUND_REQUEST
{
	unsigned char n_period;			// sound time.
	unsigned char n_time;			// times.
}snd_req;
#define SOUND_LEN	2



// e_cmd_sound_on
typedef struct SAUDIO_REQUEST
{
	unsigned char n_on;
}aud_req;
#define AUDIO_LEN	1
unsigned char pack_audio_req(char *s_out, unsigned char n_on);

// e_cmd_light_on
typedef struct SLIGHT_REQUEST
{
	unsigned char n_on;
	unsigned char n_time;			// 0 : always 1~255: 0.1 * n seconds
}light_req;
#define LIGHT_LEN	2
unsigned char pack_light_req(char *s_out, unsigned char n_on, unsigned char n_time);

typedef struct SCUSTOM_COMMAND
{
	unsigned char n_id;
	unsigned char *s_data;
}custom_req;
#define SCUSTOM_COMMAND_LEN	2
unsigned char pack_custom(char *s_out, unsigned char n_customid, unsigned char* s_data, unsigned char n_len);
unsigned char pack_setting(char *s_out, unsigned char n_settingid, unsigned char n_value);
// e_cmd_str
unsigned char pack_string(char *s_out, char *s_in, unsigned char n_strlen);
// e_cmd_battery
unsigned char pack_battery_req(char *s_out);
// e_cmd_battery_r
unsigned char pack_battery(char *s_out, unsigned char n_battery);
// e_cmd_weight
unsigned char pack_weight_req(char *s_out, unsigned char n_period, unsigned char n_time, unsigned char n_type);
// e_cmd_weight_r
// e_cmd_weight_r2
unsigned char pack_weight(char *s_out, unsigned char n_id,unsigned int n_data, unsigned char n_dp, unsigned char n_unit, unsigned char n_stable, unsigned char n_positive, unsigned char n_type);
unsigned char pack_weight2(char *s_out, unsigned char n_id,unsigned int n_data1, unsigned char n_dp1,unsigned int n_data2, unsigned char n_dp2, unsigned char n_unit, unsigned char n_stable, unsigned char n_positive, unsigned char n_type);
// e_cmd_sound
unsigned char pack_sound_req(char *s_out, unsigned char n_period, unsigned char n_time);
// e_cmd_tare
unsigned char pack_tare(char *s_out, unsigned int n_data, unsigned char n_dp, unsigned char n_unit);
#endif
