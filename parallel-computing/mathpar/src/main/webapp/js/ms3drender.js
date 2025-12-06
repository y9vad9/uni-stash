define([
  'jquery', 'util', 'three', 'three-detector', 'three-canvas',
  'three-orbit-controls', 'three-window-resize', 'three-font-helvetiker'
], function ($, util, THREEjs, THREEjsDetector, THREEjsCanvas,
             THREEjsOrbitControls, THREEjsWindowResize, THREEjsFontHelvetiker) {
  'use strict';

  /**
   * @param sectionId
   * @param $imageBelow
   * @constructor
   */
  var RenderMultipleSurfaces = function (sectionId, $imageBelow) {
    'use strict';
    var this_ = this;

    this.sectionId = sectionId;
    this.$imageBelow = $imageBelow;

    this.$imageBelow.height(0.7 * this.$imageBelow.width());

    // SCENE
    this.scene = new THREE.Scene();
    // CAMERA
    this.SCREEN_WIDTH = this.$imageBelow.width();
    this.SCREEN_HEIGHT = this.$imageBelow.height();
    this.VIEW_ANGLE = 45;
    this.ASPECT = this.SCREEN_WIDTH / this.SCREEN_HEIGHT;
    this.NEAR = 0.1;
    this.FAR = 20000;
    this.camera = new THREE.PerspectiveCamera(this.VIEW_ANGLE, this.ASPECT, this.NEAR, this.FAR);
    this.scene.add(this.camera);
    this.camera.position.set(20, 20, 10);
    this.camera.up = new THREE.Vector3(0, 0, 1);
    this.camera.lookAt(this.scene.position);
    // RENDERER
    if (Detector.webgl) {
      this.renderer = new THREE.WebGLRenderer({antialias: true, alpha: true});
    } else {
      this.renderer = new THREE.CanvasRenderer({alpha: true});
      console.log('no webgl - canvas.');
    }
    this.renderer.setSize(this.SCREEN_WIDTH, this.SCREEN_HEIGHT);
    this.renderer.setClearColor(0x000000, 0);

    // EVENTS
    // TODO: do something with window resize.
    //THREEx.WindowResize(this.renderer, this.camera);

    // CONTROLS
    this.controls = new THREE.OrbitControls(this.camera, this.renderer.domElement);

    this.$canvas = $(this.renderer.domElement);
    this.$canvas.addClass('plot3d-implicit');
    this.$canvas.width(this.$imageBelow.width());
    this.$canvas.height(this.$imageBelow.height());
    this.$canvas.insertBefore(this.$imageBelow);

    // console.log('start ajax');
    $.ajax({
      url: util.url('/api/render-surfaces'),
      type: 'POST',
      contentType: 'application/json',
      dataType: 'json',
      data: JSON.stringify({
        sectionId: this.sectionId,
        task: $('#section_' + this.sectionId).find('textarea').val()
      })
    }).done(function (response) {
      var geom = response;
     
      var numberFunction = geom[0][0];
      var surfaceFunction = [];
      var surfaceGridSize = [];
      for (var i = 0; i < numberFunction; i++) {
        surfaceFunction[i] = geom[1 + i][0];
        surfaceGridSize[i] = geom[1 + i][1];
      }

      console.log('function count: ' + numberFunction);
      
      var
          xMin = 10,
          xMax = 10,
          yMin = 10,
          yMax = 10,
          zMin = 10,
          zMax = 10,
          
          stacks = geom[2][6],

          lightX = 10,
          lightY = 12,
          lightZ = 0,
          color = geom[2][10],
          surface = geom[2][11],

          axesSize = 1.2 * Math.max(xMax, yMax, zMax);

      this_.axisHelper = new THREE.AxisHelper(axesSize);
      this_.scene.add(this_.axisHelper);

      this_.xLabel = createAxisLabel('x', new THREE.Vector3(axesSize, 0, 0), this_.camera.rotation);
      this_.yLabel = createAxisLabel('y', new THREE.Vector3(0, axesSize, 0), this_.camera.rotation);
      this_.zLabel = createAxisLabel('z', new THREE.Vector3(0, 0, axesSize), this_.camera.rotation);
      this_.scene.add(this_.xLabel);
      this_.scene.add(this_.yLabel);
      this_.scene.add(this_.zLabel);
      
      // LIGHT
      this_.light = new THREE.AmbientLight(0x404040); // soft white light
      this_.scene.add(this_.light);
      this_.lightPoint = new THREE.PointLight(0xffffff, 1.2);
      this_.lightPoint.position.set(lightX, lightY, lightZ);
      this_.scene.add(this_.lightPoint);
      
      var startIndex = 1;

      for (var i = 0; i < numberFunction; i++) {
        var colorMaterial = new THREE.MeshLambertMaterial({
          color: '#' + Math.floor(Math.random()*16777215).toString(16),
          side: THREE.DoubleSide,
          shading: THREE.SmoothShading
        });

        var vert = new Array();
        vert.push([
          surfaceFunction[i],
          surfaceGridSize[i]
        ]);

        // console.log('startIndex: ' + startIndex);
        for (var k = startIndex; k < startIndex + surfaceGridSize[i]; k++) {
          vert.push(geom[k + numberFunction]);
        }
        startIndex = k;

        this_.mesh = new THREE.Mesh(createGeometry(vert), colorMaterial);
        this_.scene.add(this_.mesh);
      }
      
      // RENDER
      this_.render();
      this_.animate();
    }).error(function () {
      alert('Render error.');
    });
  };

  function createGeometry(vert) {
    var geometry = new THREE.Geometry();
      
    // console.log(vert);
    console.log('creating geometry..');
    console.log('function: ' + vert[0][0]);
    console.log('gridsize: ' +(vert[0][1] - 1));

    var stacks = vert[1][6];

    for (var i = 2; i < vert.length; i++) {
      geometry.vertices.push(toVector3(vert[i]));
    }

    var a, b, c, d;
    var uva, uvb, uvc, uvd;
    var sliceCount = stacks + 1;

    for (var i = 0; i < stacks; i++) {

      for (var j = 0; j < stacks; j++) {
        a = i * sliceCount + j;
        b = i * sliceCount + j + 1;
        c = (i + 1) * sliceCount + j + 1;
        d = (i + 1) * sliceCount + j;

        uva = new THREE.Vector2(j / stacks, i / stacks);
        uvb = new THREE.Vector2((j + 1) / stacks, i / stacks);
        uvc = new THREE.Vector2((j + 1) / stacks, (i + 1) / stacks);
        uvd = new THREE.Vector2(j / stacks, (i + 1) / stacks);

        geometry.faces.push(new THREE.Face3(a, b, d));
        // uvs.push( [ uva, uvb, uvd ] );

        geometry.faces.push(new THREE.Face3(b, c, d));
        // uvs.push( [ uvb.clone(), uvc, uvd.clone() ] );

      }

    }

    geometry.computeFaceNormals();
    geometry.computeVertexNormals();
    return geometry;
  }

  function toVector3(vert) {
    return new THREE.Vector3(vert[0], vert[1], vert[2]);
  }

  /**
   *
   * @param {string} text text of label
   * @param {THREE.Vector3} position
   * @param {THREE.Euler} rotation
   * @returns {THREE.Mesh|*}
   */
  function createAxisLabel(text, position, rotation) {
    var textGeom, textMaterial, textMesh;
    textGeom = new THREE.TextGeometry(text, {
      size: 1,
      height: 0.2,
      curveSegments: 6,
      font: "helvetiker"
    });
    textMaterial = new THREE.MeshBasicMaterial({color: 0x999999});
    textMesh = new THREE.Mesh(textGeom, textMaterial);
    textMesh.position.x = position.x;
    textMesh.position.y = position.y;
    textMesh.position.z = position.z;
    textMesh.setRotationFromEuler(rotation);
    return textMesh;
  }

  RenderMultipleSurfaces.prototype.animate = function () {
    var this_ = this;
    requestAnimationFrame(function () {
      this_.animate();
    });
    this.render();
    this.update();
  };

  RenderMultipleSurfaces.prototype.update = function () {
    this.controls.update();
    this.xLabel.setRotationFromEuler(this.camera.rotation);
    this.yLabel.setRotationFromEuler(this.camera.rotation);
    this.zLabel.setRotationFromEuler(this.camera.rotation);
  };

  RenderMultipleSurfaces.prototype.render = function () {
    this.renderer.render(this.scene, this.camera);
  };

  return RenderMultipleSurfaces;
});
/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


