{
  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixpkgs-unstable";
    flake-parts.url = "github:hercules-ci/flake-parts";
    treefmt-nix.url = "github:numtide/treefmt-nix";
  };

  outputs =
    inputs:
    inputs.flake-parts.lib.mkFlake { inherit inputs; } {
      systems = [
        "x86_64-linux"
        "aarch64-darwin"
      ];

      imports = [
        inputs.treefmt-nix.flakeModule
      ];

      perSystem =
        { system, ... }:
        let
          overlay = final: prev: {
            jdk = prev.jdk21;
            clojure = prev.clojure.override { jdk = final.jdk; };
          };
          pkgs = import inputs.nixpkgs {
            inherit system;
            overlays = [ overlay ];
          };
        in
        {
          devShells.default = pkgs.mkShell {
            packages = with pkgs; [
              jdk
              clojure
              clj-kondo
              cljstyle
              uv
            ];
          };

          treefmt = {
            projectRootFile = "flake.nix";
            programs.nixfmt.enable = true;
          };
        };
    };
}
