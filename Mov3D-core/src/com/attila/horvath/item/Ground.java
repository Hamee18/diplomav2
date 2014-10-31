package com.attila.horvath.item;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btConvexHullShape;
import com.badlogic.gdx.physics.bullet.collision.btShapeHull;
import com.badlogic.gdx.utils.Disposable;

public class Ground extends ModelInstance implements RenderableProvider,
		Disposable {
	private final btCollisionObject body;

	public Ground(Model model, btCollisionShape shape) {
		super(model);
		body = new btCollisionObject();
		body.setCollisionShape(shape);

		setBasics();
	}

	private void setBasics() {
		this.transform.trn(0, -80, 0);
		this.transform.rotate(0, 1, 0, 45);
		body.setWorldTransform(this.transform);
		body.setCollisionFlags(body.getCollisionFlags()
				| btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
	}

	public btCollisionObject getBody() {
		return body;
	}

	public void setUserValue(int value) {
		body.setUserValue(value);
	}

	public Vector3[] getCorners() {
		BoundingBox box = new BoundingBox();
		this.calculateBoundingBox(box);

		StringBuilder sb = new StringBuilder();
		Vector3[] corners = box.getCorners();
		for(int i = 0; i < corners.length; i++) {
//			if (corners[i].x > 0) corners[i].x -= Config.MOVE/2;
//			if (corners[i].z > 0) corners[i].z -= Config.MOVE/2;
			sb.append("x:" + corners[i].x + " y: " + corners[i].y +
					" z: " + corners[i].z + "\n");
		}
		Gdx.app.log("ground", sb.toString());
		
		return box.getCorners();
	}

	@Override
	public void dispose() {
		body.dispose();
	}

	public void setTransform(float delta) {
		this.transform.trn(0, -delta, 0);
		setWordTransform();
	}

	public void setWordTransform() {
		body.setWorldTransform(this.transform);

	}

	public static class Constructor {
		private final static AssetsManager assets = new AssetsManager();
		private final Model model;

		public Constructor() {
			model = assets.getAsset("obj/Ground.g3dj");
		}

		private static btConvexHullShape createConvexHullShape(
				final Model model, boolean optimize) {
			final Mesh mesh = model.meshes.get(0);
			final btConvexHullShape shape = new btConvexHullShape(
					mesh.getVerticesBuffer(), mesh.getNumVertices(),
					mesh.getVertexSize());
			if (!optimize)
				return shape;
			// now optimize the shape
			final btShapeHull hull = new btShapeHull(shape);
			hull.buildHull(shape.getMargin());
			final btConvexHullShape result = new btConvexHullShape(hull);
			// delete the temporary shape
			shape.dispose();
			hull.dispose();
			return result;
		}

		public Ground construct() {
			return new Ground(model, createConvexHullShape(model, true));
		}
	}
}
