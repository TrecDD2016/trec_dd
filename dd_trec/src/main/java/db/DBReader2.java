package db;

import util.Constants;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DBReader2 implements DBReadService{

	/* (non-Javadoc)
	 * @see db.DBReadService#getIdf(java.lang.String)
	 */
	public double getIdf(String t) {
		PreparedStatement ps = DBUtility.getPreparedStatement("select idf from idf where term=?");
		if (ps == null) {
			return 0;
		}
		try {
			ps.setString(1, t);
			ResultSet rs  = ps.executeQuery();
			if (rs.next()) {
				return rs.getDouble(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/* (non-Javadoc)
	 * @see db.DBReadService#getPstd(java.lang.String, java.lang.String)
	 */
	public double getPstd(String t, String doc) {
		PreparedStatement dlengthPs = DBUtility.getPreparedStatement("select length from dlength where doc=?");
		PreparedStatement tdPs = DBUtility.getPreparedStatement("select td from td where term=? and doc=?");
		PreparedStatement tcPs = DBUtility.getPreparedStatement("select tc from tc where term=?");
		if (dlengthPs == null || tdPs == null || tcPs == null) {
			return 0;
		}
		try {
			dlengthPs.setString(1, doc);
			tdPs.setString(1, t);
			tdPs.setString(2, doc);
			tcPs.setString(1, t);
			
			int td = 0;
			ResultSet tdRs  = tdPs.executeQuery();
			if (tdRs.next()) {
				td = tdRs.getInt(1);
			}
			
			double tc = 0;
			ResultSet tcRs  = tcPs.executeQuery();
			if (tcRs.next()) {
				tc = tdRs.getDouble(1);
			} 
			
			int dlength = 0;
			ResultSet dlengthRs  =dlengthPs.executeQuery();
			if (dlengthRs.next()) {
				dlength = dlengthRs.getInt(1);
			}
			
			tdRs.close();
			tcRs.close();
			dlengthRs.close();
			
			return (td + Constants.u * tc) / ((double)dlength + Constants.u);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/* (non-Javadoc)
	 * @see db.DBReadService#getPustd(java.lang.String, java.lang.String)
	 */
	public double getPustd(String t, String doc) {
		PreparedStatement dlengthPs = DBUtility.getPreparedStatement("select length from dlength where doc=?");
		PreparedStatement tdPs = DBUtility.getPreparedStatement("select td from td where term=? and doc=?");
		if (dlengthPs == null || tdPs == null) {
			return 0;
		}
		try {
			dlengthPs.setString(1, doc);
			tdPs.setString(1, t);
			tdPs.setString(2, doc);
			
			int td = 0;
			ResultSet tdRs  = tdPs.executeQuery();
			if (tdRs.next()) {
				td = tdRs.getInt(1);
			}
			
			int dlength = 0;
			ResultSet dlengthRs  =dlengthPs.executeQuery();
			if (dlengthRs.next()) {
				dlength = dlengthRs.getInt(1);
			}
			
			tdRs.close();
			dlengthRs.close();
			
			return (td ) / ((double)dlength);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/* (non-Javadoc)
	 * @see db.DBReadService#getRelatedDocs(java.util.ArrayList)
	 */
	public ArrayList<String> getRelatedDocs(ArrayList<String> terms) {
		ArrayList<String> res = new ArrayList<String>();
		if (terms == null || terms.size() == 0) {
			return res;
		}
		StringBuffer sql = new StringBuffer("select distinct doc from td where term in (");
		for (int i=0;i<terms.size();i++) {
			sql.append("?,");
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(')');
		PreparedStatement ps = DBUtility.getPreparedStatement(sql.toString());
		try {
			for (int i=0;i<terms.size();i++) {
				ps.setString(i + 1, terms.get(i));
			}
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				res.add(rs.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}
	
	public static void main(String[]args) {
		ArrayList<String> a = new ArrayList<String>();
		a.add("clueweb09-en0000-23-00194");
		a.add("clueweb09-en0000-43-16967");
		ArrayList<String> res = new DBReader2().getRelatedDocs(a);
		for (String s : res) {
			System.out.println(s);
		}
		System.out.println(new DBReader2().getIdf("contact"));
		System.out.println(new DBReader2().getPstd("contact", "clueweb09-en0000-43-16967"));
		System.out.println(new DBReader2().getPustd("contact", "clueweb09-en0000-43-16967"));
	}

	/* (non-Javadoc)
	 * @see db.DBReadService#getPaths(java.util.ArrayList)
	 */
	public ArrayList<String> getPaths(ArrayList<String> docs) {
		ArrayList<String> res = new ArrayList<String>();
		if (docs == null || docs.size() == 0) {
			return res;
		}
		StringBuffer sql = new StringBuffer("select path from path where doc in (");
		for (int i=0;i<docs.size();i++) {
			sql.append("?,");
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(')');
		PreparedStatement ps = DBUtility.getPreparedStatement(sql.toString());
		try {
			for (int i=0;i<docs.size();i++) {
				ps.setString(i + 1, docs.get(i));
			}
			ResultSet rs = ps.executeQuery();
			while(rs.next()) {
				res.add(rs.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}

	/* (non-Javadoc)
	 * @see db.DBReadService#initialize(java.lang.String)
	 */
	public void initialize(ArrayList<String> query) {
		// TODO Auto-generated method stub
	}

}
