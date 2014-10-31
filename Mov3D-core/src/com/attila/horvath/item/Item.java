package com.attila.horvath.item;

import java.util.Random;

import com.attila.horvath.config.Config;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.RenderableProvider;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btConvexHullShape;
import com.badlogic.gdx.physics.bullet.collision.btShapeHull;
import com.badlogic.gdx.utils.Disposable;

public class Item extends ModelInstance implements RenderableProvider, Disposable {

	private final btCollisionObject body;
	private boolean moving = false;

	public Item(Model model, btCollisionShape shape) {
		super(model);
		body = new btCollisionObject();
		body.setCollisionShape(shape);
		
		setBasics();
	}

	private void setBasics() {
//		this.transform.trn(-Config.MOVE / 2, 154, -Config.MOVE / 2);
		this.transform.trn(0, 142f, 0);
		this.transform.rotate(0, 1, 0, 45);
		body.setWorldTransform(this.transform);
		body.setCollisionFlags(body.getCollisionFlags()
				| btCollisionObject.CollisionFlags.CF_CUSTOM_MATERIAL_CALLBACK);
	}

	public btCollisionObject getBody() {
		return body;
	}

	public boolean getMoving() {
		return moving;
	}

	public void setMoving(boolean moving) {
		this.moving = moving;
	}

	public void setUserValue(int value) {
		body.setUserValue(value);
	}

	@Override
	public void dispose() {
		body.dispose();
	}

	public Vector3[] getCorners() {
		BoundingBox box = new BoundingBox();
		this.calculateBoundingBox(box);

		StringBuilder sb = new StringBuilder();
		Vector3[] corners = box.getCorners();
		for(int i = 0; i < corners.length; i++) {
			sb.append("x:" + corners[i].x + " y: " + corners[i].y +
					" z: " + corners[i].z + "\n");
		}
		Gdx.app.log("Item", sb.toString());
		
		return box.getCorners();
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
		private Random randomColor = new Random();
		private final Color colors[] = { Color.CYAN,
				Color.GRAY, Color.GREEN, Color.LIGHT_GRAY,
				Color.ORANGE, Color.PINK, Color.WHITE, Color.YELLOW };
		private final Model model;

		public Constructor(String obj) {
			model = assets.getAsset("obj/" + obj + ".g3dj");
			model.getMaterial("Material").set(
					ColorAttribute.createDiffuse(colors[randomColor
							.nextInt(colors.length - 1)]));
	
			Gdx.app.log("Mesh num", String.valueOf(model.meshes.size));
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

		public Item construct() {
			return new Item(model, createConvexHullShape(model, true));
		}
	}
}
