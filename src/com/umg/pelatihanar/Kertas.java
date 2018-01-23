package com.umg.pelatihanar;


import java.nio.Buffer;

import com.qualcomm.vuforia.samples.SampleApplication.utils.MeshObject;
import com.qualcomm.vuforia.samples.SampleApplication.utils.MeshObject.BUFFER_TYPE;

public class Kertas extends MeshObject {

	private Buffer mVertBuff;
	private Buffer mTexCoordBuff;
	private Buffer mNormBuff;
	private Buffer mIndBuff;

	private int indicesNumber = 0;
	private int verticesNumber = 0;

	public Kertas() {
		setVerts();
		setTexCoords();
		setNorms();
		setIndices();
	}

	private void setVerts() {
		double[] TEAPOT_VERTS = { 
				-64.000000f, -64.000000f, 0.000000f,
				64.000000f, -64.000000f, 0.000000f,
				64.000000f, 128.000000f,	0.000000f,
				-64.000000f, 128.000000f, 0.000000f };
		mVertBuff = fillBuffer(TEAPOT_VERTS);
		verticesNumber = TEAPOT_VERTS.length / 3;
	}

	private void setTexCoords() {
		double[] KERTAS_TEX_COORDS = { 0.000000f, 0.000000f, 1.000000f,
				0.000000f, 1.000000f, 1.000000f, 0.000000f, 1.000000f };
		mTexCoordBuff = fillBuffer(KERTAS_TEX_COORDS);

	}

	private void setNorms() {
		double[] KERTAS_NORMS = { 0.000000f, 0.000000f, 1.000000f, 0.000000f,
				0.000000f, 1.000000f, 0.000000f, 0.000000f, 1.000000f,
				0.000000f, 0.000000f, 1.000000f };
		mNormBuff = fillBuffer(KERTAS_NORMS);
	}

	private void setIndices() {
		short[] KERTAS_INDICES = {0, 1, 2, 0, 2, 3 };
		mIndBuff = fillBuffer(KERTAS_INDICES);
		indicesNumber = KERTAS_INDICES.length;
	}

	public int getNumObjectIndex() {
		return indicesNumber;
	}

	@Override
	public int getNumObjectVertex() {
		return verticesNumber;
	}

	@Override
	public Buffer getBuffer(BUFFER_TYPE bufferType) {
		Buffer result = null;
		switch (bufferType) {
		case BUFFER_TYPE_VERTEX:
			result = mVertBuff;
			break;
		case BUFFER_TYPE_TEXTURE_COORD:
			result = mTexCoordBuff;
			break;
		case BUFFER_TYPE_NORMALS:
			result = mNormBuff;
			break;
		case BUFFER_TYPE_INDICES:
			result = mIndBuff;
		default:
			break;

		}

		return result;
	}

}
