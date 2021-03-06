package setflag.utils;


import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
 
/**
 * APP登录Token的生成和解析
 * 
 */
public class JwtToken {
 
	/** token秘钥，请勿泄露，请勿随便修改 backups:JKKLJOoasdlfj */
	public static final String SECRET = "JKKLJOoasdlfj";
	/** token 过期时间: 10天 */
	public static final int calendarField = Calendar.DATE;
	public static final int calendarInterval = 10;
 
	/**
	 * JWT生成Token.
	 * 
	 * JWT构成: header, payload, signature
	 * 
	 * @param user_id
	 *            登录成功后用户user_id, 参数user_id不可传空
	 */
	public static String createToken(String user_id) throws Exception {
		Date iatDate = new Date();
		// expire time
		Calendar nowTime = Calendar.getInstance();
		nowTime.add(calendarField, calendarInterval);
		Date expiresDate = nowTime.getTime();
 
		// header Map
		Map<String, Object> map = new HashMap<>();
		map.put("alg", "HS256");
		map.put("typ", "JWT");
 
		// build token
		// param backups {iss:Service, aud:APP}
		String token = JWT.create().withHeader(map) // header
				.withClaim("iss", "Service") // payload
				.withClaim("aud", "APP").withClaim("user_id", null == user_id ? null : user_id.toString())
				.withIssuedAt(iatDate) // sign time
				.withExpiresAt(expiresDate) // expire time
				.sign(Algorithm.HMAC256(SECRET)); // signature
 
		return token;
	}
 
	/**
	 * 解密Token
	 * 
	 * @param token
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Claim> verifyToken(String token) {
		DecodedJWT jwt = null;
		try {
			JWTVerifier verifier = JWT.require(Algorithm.HMAC256(SECRET)).build();
			jwt = verifier.verify(token);
		} catch (Exception e) {
			 e.printStackTrace();
			// token 校验失败, 抛出Token验证非法异常
			 Map<String, Claim> map = new HashMap<String, Claim>();
			 map.put("user_id", null);
			 return map;
		}
		return jwt.getClaims();
	}
 
	/**
	 * 根据Token获取user_id
	 * 
	 * @param token
	 * @return user_id
	 */
	public static String getAppUID(String token) {
		Map<String, Claim> claims = verifyToken(token);
		Claim user_id_claim = claims.get("user_id");
		if (null == user_id_claim || StringUtils.isEmpty(user_id_claim.asString())) {
			// token 校验失败, 抛出Token验证非法异常
			return "false";
		}
		return user_id_claim.asString();
	}
	
	
	public static void main(String[] args) {
		try {
			System.out.println(createToken("dwadwadewra"));
			System.out.println(getAppUID("eyJhbGcJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOiJBUFAiLCJ1c2VyX2lkIjoiM0VERUQ3OTJENzgwNDg4OUI2RkEwNzIwOUVFQjgwMTYiLCJpc3MiOiJTZXJ2aWNlIiwiZXhwIjoxNTU3MjQyNzg3LCJpYXQiOjE1NTYzNzg3ODd9.kZ9vddFGJj77Aut1i14jYP-MKTm061fz4Auh0iqTeP8" ));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
