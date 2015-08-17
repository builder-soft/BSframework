package cl.buildersoft.framework.util;

import java.io.File;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import cl.buildersoft.framework.database.BSmySQL;
import cl.buildersoft.framework.exception.BSSystemException;

public class BSFileUtil {

	public Map<String, String> uploadFile(HttpServletRequest request) {
		BSmySQL mysql = new BSmySQL();
		Connection conn = mysql.getConnection(request);

		BSConfig config = new BSConfig();
		String tempPath = fixPath(config.getString(conn, "EMPLOYEE_FILES"));
		mysql.closeConnection(conn);
		
		String fileName = "" + System.currentTimeMillis();
		Map<String, String> values = (HashMap<String, String>) uploadFile(request, tempPath, fileName);

		values.put("file.path", tempPath);
		values.put("file.fileRealName", fileName);

		return values;
	}

	@Deprecated
	public String fixPath(String path) {
		String fileSeparator = BSConfig.getFileSeparator();
		if (path.lastIndexOf(fileSeparator) < path.length()) {
			path += fileSeparator;
		}
		return path;
	}

	public Map<String, String> uploadFile(HttpServletRequest request, String path, String fileName) {
		Map<String, String> out = new HashMap<String, String>();
		DiskFileItemFactory factory = new DiskFileItemFactory();

		ServletFileUpload upload = new ServletFileUpload(factory);

		List<FileItem> items = null;
		try {
			items = upload.parseRequest(request);

			for (FileItem item : items) {
				if (item.isFormField()) {
					out.put(item.getFieldName(), item.getString());

				} else {
					out.put("file.fieldName", item.getFieldName());
					out.put("file.fileName", item.getName());
					out.put("file.contentType", item.getContentType());
					out.put("file.size", "" + item.getSize());

					File file = new File(path + fileName);
					item.write(file);
				}
			}
		} catch (Exception e) {
			throw new BSSystemException(e);
		}
		return out;
	}

	public Boolean moveFile(String oldPath, String oldName, String newPath, String newName) {
		File oldFile = new File(oldPath + oldName);
		File newFile = new File(newPath + newName);
		Boolean success = oldFile.renameTo(newFile);
		return success;
	}

	public String getPath(String path, Long category) {
		String fullPath = path + category;
		File folder = new File(fullPath);

		if (!folder.exists()) {
			folder.mkdirs();
		}

		return fullPath;
	}


}
