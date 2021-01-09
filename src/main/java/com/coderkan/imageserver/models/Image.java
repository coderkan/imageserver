package com.coderkan.imageserver.models;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import com.coderkan.imageserver.helpers.FileNameHelper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_image", schema = "public")
public class Image extends BaseEntity {

	@Column(name = "file_name")
	private String fileName;

	@Column(name = "file_type")
	private String fileType;

	@Column(name = "size")
	private long size;

	@Column(name = "uuid")
	private String uuid;

	@Column(name = "system_name")
	private String systemName;

	@Lob
	@Column(name = "data")
	@Type(type = "org.hibernate.type.BinaryType")
	private byte[] data;

	/**
	 * Create new Image class.
	 * 
	 * @return new Image.
	 */
	@Transient
	public static Image build() {
		String uuid = UUID.randomUUID().toString();
		Image image = new Image();
		Date now = new Date();
		image.setUuid(uuid);
		image.setCreatedDate(now);
		image.setUpdatedDate(now);
		image.setCreatedBy("default");
		image.setSystemName("default");
		image.setUpdatedBy("default");
		image.setStatus(true);
		return image;
	}

	@Transient
	public void setFiles(MultipartFile file) {
		setFileType(file.getContentType());
		setSize(file.getSize());
	}

	/**
	 * Scale image data with given width and height.
	 * 
	 * @param width  scale width
	 * @param height scale height
	 * @return scaled image byte array and change to class data.
	 */
	@Transient
	public byte[] scale(int width, int height) throws Exception {

		if (width == 0 || height == 0)
			return data;

		ByteArrayInputStream in = new ByteArrayInputStream(data);

		try {
			BufferedImage img = ImageIO.read(in);

			java.awt.Image scaledImage = img.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
			BufferedImage imgBuff = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			imgBuff.getGraphics().drawImage(scaledImage, 0, 0, new Color(0, 0, 0), null);
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();

			ImageIO.write(imgBuff, "jpg", buffer);
			setData(buffer.toByteArray());
			return buffer.toByteArray();

		} catch (Exception e) {
			throw new Exception("IOException in scale");
		}
	}

	/**
	 * @param fileName - filename of the resources.
	 * 
	 * @return inputstream for image
	 * */
	private static InputStream getResourceFileAsInputStream(String fileName) {
	    ClassLoader classLoader = Image.class.getClassLoader();
	    return classLoader.getResourceAsStream(fileName);
	}
	
	/**
	 * Generate no context image with `notfound.jpg` image in asset.
	 * 
	 * @return create default image.
	 */
	@Transient
	public static Image defaultImage() throws Exception {
	    InputStream is = getResourceFileAsInputStream("notfound.jpg");
		String fileType = "image/jpeg";
		byte[] bdata = FileCopyUtils.copyToByteArray(is);
		Image image = new Image(null, fileType, 0, null, null, bdata);
		return image;
	}

	/**
	 * Generate scaled no context image with `notfound.jpg` image in asset with
	 * given width and height.
	 * 
	 * @param width  scale width
	 * @param height scale height
	 * @return create scaled default image.
	 */
	@Transient
	public static Image defaultImage(int width, int height) throws Exception {
		Image defaultImage = defaultImage();
		defaultImage.scale(width, height);
		return defaultImage;
	}

	/**
	 * Generate scaled no context image with `notfound.jpg` image in asset with
	 * given width and height.
	 * 
	 * @param file   multipartfile data to build.
	 * @param helper filenamehelper class to generate name.
	 * @return return new Image class related with file.
	 */
	@Transient
	public static Image buildImage(MultipartFile file, FileNameHelper helper) {
		String fileName = helper.generateDisplayName(file.getOriginalFilename());

		Image image = Image.build();
		image.setFileName(fileName);
		image.setFiles(file);

		try {
			image.setData(file.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}

}