package com.umg.pelatihanar;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.Matrix;
import android.util.Log;

import com.qualcomm.vuforia.Matrix44F;
import com.qualcomm.vuforia.Renderer;
import com.qualcomm.vuforia.State;
import com.qualcomm.vuforia.Tool;
import com.qualcomm.vuforia.Trackable;
import com.qualcomm.vuforia.TrackableResult;
import com.qualcomm.vuforia.VIDEO_BACKGROUND_REFLECTION;
import com.qualcomm.vuforia.Vuforia;
import com.qualcomm.vuforia.samples.SampleApplication.SampleApplicationSession;
import com.qualcomm.vuforia.samples.SampleApplication.utils.CubeShaders;
import com.qualcomm.vuforia.samples.SampleApplication.utils.LoadingDialogHandler;
import com.qualcomm.vuforia.samples.SampleApplication.utils.SampleApplication3DModel;
import com.qualcomm.vuforia.samples.SampleApplication.utils.SampleUtils;
import com.qualcomm.vuforia.samples.SampleApplication.utils.Texture;
 
public class ImageTargetRenderer implements GLSurfaceView.Renderer {
	private static final String LOGTAG = "ImageTargetRenderer";

	private SampleApplicationSession vuforiaSession;
	private ImageTargets mActivity;
	private Vector<Texture> mTextures;
	private int shaderProgramID;
	private int vertexHandle;
	private int normalHandle;
	private int textureCoordHandle;
	private int mvpMatrixHandle;
	private int texSampler2DHandle;
	private Kertas mKertas;
	private float kBuildingScale = 12.0f;
	private Renderer mRenderer;
	boolean mIsActive = false;
	private static final float OBJECT_SCALE_FLOAT = 3.0f;

	// ///
	public ImageTargetRenderer(ImageTargets activity,
			SampleApplicationSession session) {
		mActivity = activity;
		vuforiaSession = session;
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		//method ini akan dieksekusi terus menerus
		if (!mIsActive)
			return;
		renderFrame();
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		//method ini dipanggil pertama kali
		initRendering();
		vuforiaSession.onSurfaceCreated();
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		vuforiaSession.onSurfaceChanged(width, height);
	}

	private void initRendering() {
		mKertas = new Kertas();
		mRenderer = Renderer.getInstance();

		GLES20.glClearColor(0.0f, 0.0f, 0.0f, Vuforia.requiresAlpha() ? 0.0f
				: 1.0f);

		for (Texture t : mTextures) {
			GLES20.glGenTextures(1, t.mTextureID, 0);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, t.mTextureID[0]);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
					GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
			GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
					GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
			GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA,
					t.mWidth, t.mHeight, 0, GLES20.GL_RGBA,
					GLES20.GL_UNSIGNED_BYTE, t.mData);
		}

		shaderProgramID = SampleUtils.createProgramFromShaderSrc(
				CubeShaders.CUBE_MESH_VERTEX_SHADER,
				CubeShaders.CUBE_MESH_FRAGMENT_SHADER);

		vertexHandle = GLES20.glGetAttribLocation(shaderProgramID,
				"vertexPosition");
		normalHandle = GLES20.glGetAttribLocation(shaderProgramID,
				"vertexNormal");
		textureCoordHandle = GLES20.glGetAttribLocation(shaderProgramID,
				"vertexTexCoord");
		mvpMatrixHandle = GLES20.glGetUniformLocation(shaderProgramID,
				"modelViewProjectionMatrix");
		texSampler2DHandle = GLES20.glGetUniformLocation(shaderProgramID,
				"texSampler2D");

		// dialog loading
		mActivity.loadingDialogHandler
				.sendEmptyMessage(LoadingDialogHandler.HIDE_LOADING_DIALOG);

	}

	private void renderFrame() {
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		State state = mRenderer.begin();
		mRenderer.drawVideoBackground();

		GLES20.glEnable(GLES20.GL_DEPTH_TEST);

		GLES20.glEnable(GLES20.GL_CULL_FACE);
		GLES20.glCullFace(GLES20.GL_BACK);
		if (Renderer.getInstance().getVideoBackgroundConfig().getReflection() == VIDEO_BACKGROUND_REFLECTION.VIDEO_BACKGROUND_REFLECTION_ON)
			GLES20.glFrontFace(GLES20.GL_CW); // Front camera
		else
			GLES20.glFrontFace(GLES20.GL_CCW); // Back camera

		// Jika marker terdeteksi pada perulangan
		for (int tIdx = 0; tIdx < state.getNumTrackableResults(); tIdx++) {
			TrackableResult hasil_tracking = state.getTrackableResult(tIdx);
			Trackable marker = hasil_tracking.getTrackable();

			Log.d(LOGTAG, "Marker terdeteksi:" + marker);

			Matrix44F modelViewMatrix_Vuforia = Tool
					.convertPose2GLMatrix(hasil_tracking.getPose());
			float[] modelViewMatrix = modelViewMatrix_Vuforia.getData();

			///////////////////////////start deklarasi teksture//////////////////////////
			int indexTexture = 0;
			if(marker.getName().equalsIgnoreCase("TeknikElektro")){
				indexTexture=0;
			}
			else if(marker.getName().equalsIgnoreCase("TeknikIndustri")){
				indexTexture=1;
			}else{
				indexTexture=2;
			}
			///////////////////////////end deklarasi teksture//////////////////////////

			float[] modelViewProjection = new float[16];

			if (!mActivity.isExtendedTrackingActive()) {
				Matrix.translateM(modelViewMatrix, 0, 0.0f, 0.0f,
						OBJECT_SCALE_FLOAT);
				Matrix.scaleM(modelViewMatrix, 0, OBJECT_SCALE_FLOAT,
						OBJECT_SCALE_FLOAT, OBJECT_SCALE_FLOAT);
			} else {
				Matrix.rotateM(modelViewMatrix, 0, 90.0f, 1.0f, 0, 0);
				Matrix.scaleM(modelViewMatrix, 0, kBuildingScale,
						kBuildingScale, kBuildingScale);
			}

			Matrix.multiplyMM(modelViewProjection, 0, vuforiaSession
					.getProjectionMatrix().getData(), 0, modelViewMatrix, 0);

			GLES20.glUseProgram(shaderProgramID);

			// /////////////////start untuk menggambar kertas///////////////////////
			GLES20.glVertexAttribPointer(vertexHandle, 3, GLES20.GL_FLOAT,
					false, 0, mKertas.getVertices());
			GLES20.glVertexAttribPointer(normalHandle, 3, GLES20.GL_FLOAT,
					false, 0, mKertas.getNormals());
			GLES20.glVertexAttribPointer(textureCoordHandle, 2,
					GLES20.GL_FLOAT, false, 0, mKertas.getTexCoords());

			// enable dulu
			GLES20.glEnableVertexAttribArray(vertexHandle);
			GLES20.glEnableVertexAttribArray(normalHandle);
			GLES20.glEnableVertexAttribArray(textureCoordHandle);

			GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,
					mTextures.get(indexTexture).mTextureID[0]);
			GLES20.glUniform1i(texSampler2DHandle, 0);

			GLES20.glUniformMatrix4fv(mvpMatrixHandle, 1, false,
					modelViewProjection, 0);

			// gambar kertas
			GLES20.glDrawElements(GLES20.GL_TRIANGLES,
					mKertas.getNumObjectIndex(), GLES20.GL_UNSIGNED_SHORT,
					mKertas.getIndices());

			// disable
			GLES20.glDisableVertexAttribArray(vertexHandle);
			GLES20.glDisableVertexAttribArray(normalHandle);
			GLES20.glDisableVertexAttribArray(textureCoordHandle);

			// /////////////////end untuk menggambar kertas///////////////////////

			SampleUtils.checkGLError("Render Frame");

		}

		GLES20.glDisable(GLES20.GL_DEPTH_TEST);

		mRenderer.end();
	}

	public void setTextures(Vector<Texture> textures) {
		mTextures = textures;
	}

}